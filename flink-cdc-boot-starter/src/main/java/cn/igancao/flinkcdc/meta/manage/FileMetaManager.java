package cn.igancao.flinkcdc.meta.manage;

import cn.hutool.core.collection.CollUtil;
import cn.igancao.flinkcdc.common.exception.FlinkCDCException;
import cn.igancao.flinkcdc.holder.FlinkJobPropertiesHolder;
import cn.igancao.flinkcdc.meta.FlinkJobIdentity;
import cn.igancao.flinkcdc.meta.LogPosition;
import cn.igancao.flinkcdc.properties.FlinkJobProperties;
import cn.igancao.flinkcdc.properties.FlinkProperties;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 文件存储cursor管理
 *
 * 1. 先写内存，然后定时刷新数据到File
 * 2. 数据采取overwrite模式(只保留最后一次)
 */
@Component
@Slf4j
public class FileMetaManager extends MemoryMetaManager implements MetaManager, Serializable {

    @Autowired
    private FlinkProperties flinkProperties;

    //文件存放路径
    private File dataDir;

    private String dataFilePrefix = "flink-cdc";
    private String dataFileName = "meta.dat";

    private static final String dataDirEndWith = "/**/meta.dat";

    /**
     * flinkJob 游标文件map
     */
    private Map<FlinkJobIdentity, File> dataFileCaches;

    private long period = 1000;

    private ScheduledExecutorService executor;

    /**
     * 待记录的游标任务
     */
    private final Set<FlinkJobIdentity> updateCursorTasks =  new CopyOnWriteArraySet<>();


    @Override
    public void start() {
        super.start();
        //设置文件存储位置
        FlinkProperties.Meta meta = flinkProperties.getMeta();
        setDataDir(meta.getDataDir());

        if (!dataDir.exists()) {
            try {
                FileUtils.forceMkdir(dataDir);
            } catch (IOException e) {
                throw new FlinkCDCException("创建文件路径" + dataDir + "失败!");
            }
        }

        if (!dataDir.canRead() || !dataDir.canWrite()) {
            throw new FlinkCDCException("文件路径" + dataDir + "不可读写!");
        }

        log.info("flink-cdc meta data dir is: {}", dataDir);

        initDataFileCaches();
        loadCursor();

        executor = Executors.newScheduledThreadPool(1, new CustomizableThreadFactory("flink-cdc-meta-file-"));
        // 启动定时工作任务
        executor.scheduleAtFixedRate(() -> {
                    List<FlinkJobIdentity> tasks = new ArrayList<>(updateCursorTasks);
                    for (FlinkJobIdentity flinkJobIdentity : tasks) {
                        try {
                            updateCursorTasks.remove(flinkJobIdentity);

                            // 定时将内存中的最新值刷到file中，多次变更只刷一次
                            flushDataToFile(flinkJobIdentity);
                        } catch (Throwable e) {
                            // ignore
                            log.error("period update [" + flinkJobIdentity.toString() + "] curosr failed!", e.getMessage());
                        }
                    }
                },
                period,
                period,
                TimeUnit.MILLISECONDS);

    }

    public void stop() {
        flushDataToFile();// 刷新数据
        super.stop();
        executor.shutdownNow();
    }

    @Override
    public void updateCursor(FlinkJobIdentity flinkJobIdentity, LogPosition position) {
        super.updateCursor(flinkJobIdentity, position);
        updateCursorTasks.add(flinkJobIdentity);
    }

    private void flushDataToFile() {
        for (FlinkJobIdentity flinkJobIdentity : cursors.keySet()) {
            flushDataToFile(flinkJobIdentity);
        }
    }

    /**
     * <p>将内存中的 FlinkJob cursor 持久化</p>
     *
     * @param flinkJobIdentity FlinkJob 唯一标识
     */
    private void flushDataToFile(FlinkJobIdentity flinkJobIdentity) {
        flushDataToFile(flinkJobIdentity, dataFileCaches.get(flinkJobIdentity));
    }

    private void flushDataToFile(FlinkJobIdentity flinkJobIdentity, File dataFile) {

        LogPosition logPosition = this.cursors.get(flinkJobIdentity);
        String json = JSON.toJSONString(logPosition);
        try {
            FileUtils.writeStringToFile(dataFile, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FlinkCDCException(e);
        }

    }

    /**
     * 启动时加载 dataDir 路径下的 cursor 到内存，
     * 使得 FlinkJob 可以接着上次 cursor 继续监听 binlog
     */
    private void loadCursor() {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(new FileSystemResourceLoader());
        try {
            Resource[] resources = resourcePatternResolver.getResources("file:" + dataDir.getPath() + dataDirEndWith);
            if (ObjectUtils.isEmpty(resources)) return;

            for (Resource resource : resources) {
                String content = getContent(resource);

                LogPosition logPosition = JSON.parseObject(content, LogPosition.class);
                this.cursors.put(logPosition.getFlinkJobIdentity(), logPosition);
            }

        } catch (IOException e) {
            throw new FlinkCDCException(e);
        }
    }

    /**
     * 解析资源文件
     */
    private String getContent(Resource resource) {
        try {
            EncodedResource encodedResource = new EncodedResource(resource, StandardCharsets.UTF_8);
            // 字符输入流
            try (Reader reader = encodedResource.getReader()) {
                return IOUtils.toString(reader);
            }
        } catch (IOException e) {
            throw new FlinkCDCException(e);
        }
    }

    /**
     * 启动时为每个 FlinkJob 创建 meta.dat 的存放路径
     */
    private void initDataFileCaches() {
        dataFileCaches = new ConcurrentHashMap<>();
        if (CollUtil.isEmpty(FlinkJobPropertiesHolder.getPropertiesMap())){
            return;
        }

        for (FlinkJobProperties property : FlinkJobPropertiesHolder.getPropertiesMap().values()) {
            FlinkProperties.Meta meta = flinkProperties.getMeta();
            FlinkJobIdentity flinkJobIdentity = FlinkJobIdentity.generate(meta, property.getName());
            File file = new File(dataDir, File.separator + flinkJobIdentity.getApplicationName() + flinkJobIdentity.getPort() + File.separator + flinkJobIdentity.getFlinkJobName() + File.separator + dataFileName);
            dataFileCaches.put(flinkJobIdentity, file);
        }
    }

    public void setDataDir(String dataDir) {
        dataDir = dataDir + File.separator + dataFilePrefix;
        this.dataDir = new File(dataDir);
    }

    @Override
    public String getName() {
        return "file";
    }
}

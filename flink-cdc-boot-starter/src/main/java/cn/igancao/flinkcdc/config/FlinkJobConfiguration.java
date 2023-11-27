package cn.igancao.flinkcdc.config;

import cn.hutool.core.collection.CollUtil;
import cn.igancao.flinkcdc.annotation.FlinkSink;
import cn.igancao.flinkcdc.common.contants.BaseConstants;
import cn.igancao.flinkcdc.common.contants.FlinkOrdered;
import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.exception.FlinkCDCException;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;
import cn.igancao.flinkcdc.deserialize.MysqlDeserialization;
import cn.igancao.flinkcdc.holder.FlinkJobBus;
import cn.igancao.flinkcdc.holder.FlinkJobPropertiesHolder;
import cn.igancao.flinkcdc.holder.FlinkSinkHolder;
import cn.igancao.flinkcdc.meta.FlinkJobIdentity;
import cn.igancao.flinkcdc.meta.LogPosition;
import cn.igancao.flinkcdc.meta.manage.MetaManager;
import cn.igancao.flinkcdc.properties.FlinkJobProperties;
import cn.igancao.flinkcdc.properties.FlinkProperties;
import cn.igancao.flinkcdc.proxy.FlinkSinkProxy;
import com.google.common.base.Throwables;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * flink job 管理
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = BaseConstants.ENABLE_PREFIX, havingValue = "true")
public class FlinkJobConfiguration implements ApplicationContextAware, SmartInitializingSingleton, Ordered {

    private ApplicationContext applicationContext;

    @Autowired
    private FlinkProperties flinkProperties;

    @Override
    public void afterSingletonsInstantiated() {
        //获取flink sink信息
        initSink();

        //初始化flink job
        for (FlinkJobProperties flinkJobProperty : FlinkJobPropertiesHolder.getPropertiesMap().values()) {
            try {
                initFlinkJob(flinkJobProperty);
            } catch (Exception e) {
                log.error("init flink job failed, error:[{}]", Throwables.getStackTraceAsString(e));
                throw new FlinkCDCException("init flink job failed!");
            }
        }
    }

    /**
     * 初始化flink sink
     */
    private void initSink() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(FlinkSink.class);
        for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
            if (entry.getValue() instanceof FlinkJobSink) {
                FlinkSink flinkSink = entry.getValue().getClass().getAnnotation(FlinkSink.class);
                FlinkSinkHolder.registerSink((FlinkJobSink) entry.getValue(), flinkSink);
            }
        }
    }

    /**
     * 初始化flink job
     */
    private void initFlinkJob(FlinkJobProperties flinkJobProperty) throws Exception {
        List<FlinkJobSink> jobSinkList = FlinkSinkHolder.getSink(flinkJobProperty.getName());
        if (CollUtil.isEmpty(jobSinkList)) {
            return;
        }
        FlinkJobIdentity flinkJobIdentity = FlinkJobIdentity.generate(flinkProperties.getMeta(), flinkJobProperty.getName());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE);



        MySqlSource<DataChangeInfo> source = buildDataChangeSource(flinkJobProperty, flinkJobIdentity);
        DataStreamSource<DataChangeInfo> streamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "mysql-source");
        streamSource.setParallelism(1);//设置并行度为 1,确保数据的顺序性

        FlinkJobSink flinkJobSink = (FlinkJobSink) Proxy.newProxyInstance(FlinkJobSink.class.getClassLoader(), new Class<?>[]{FlinkJobSink.class},
                new FlinkSinkProxy(flinkJobIdentity));
        streamSource.addSink(flinkJobSink);

        env.executeAsync();

        log.info("flink job [{}] start success", flinkJobProperty.getName());
    }

    /**
     * 构建变更数据源
     */
    private MySqlSource<DataChangeInfo> buildDataChangeSource(FlinkJobProperties flinkJobProperty, FlinkJobIdentity flinkJobIdentity) {
        MetaManager metaManager = FlinkJobBus.getMetaManager();
        LogPosition cursor = metaManager.getCursor(flinkJobIdentity);
        StartupOptions startupOptions = null;
        if (cursor != null) {
            startupOptions = StartupOptions.timestamp(cursor.getStartupTimestampMillis() + 1);
        }
        return MySqlSource.<DataChangeInfo>builder()
                .hostname(flinkJobProperty.getHostname())
                .port(flinkJobProperty.getPort())
                .databaseList(flinkJobProperty.getDatabaseList())
                .tableList(flinkJobProperty.getTableList())
                .username(flinkJobProperty.getUsername())
                .password(flinkJobProperty.getPassword())
                .startupOptions(startupOptions != null ? startupOptions : flinkJobProperty.getStartupOptions())
                .debeziumProperties(getDebeziumProperties())
                .includeSchemaChanges(true)
                .deserializer(new MysqlDeserialization())
                .serverTimeZone(flinkJobProperty.getServerTimeZone())
                .build();
    }

    private static Properties getDebeziumProperties(){
        Properties properties = new Properties();
        properties.setProperty("converters", "dateConverters");
        //根据类在那个包下面修改
        properties.setProperty("dateConverters.type", "cn.igancao.flinkcdc.deserialize.MySqlDateTimeConverter");
        properties.setProperty("dateConverters.format.date", "yyyy-MM-dd");
        properties.setProperty("dateConverters.format.time", "HH:mm:ss");
        properties.setProperty("dateConverters.format.datetime", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("dateConverters.format.timestamp", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("dateConverters.format.timestamp.zone", "UTC+8");
        properties.setProperty("debezium.snapshot.locking.mode","none"); //全局读写锁，可能会影响在线业务，跳过锁设置
        properties.setProperty("include.schema.changes", "true");
        properties.setProperty("bigint.unsigned.handling.mode","long");
        properties.setProperty("decimal.handling.mode","double");
        return properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return FlinkOrdered.ORDER_LISTENER;
    }
}

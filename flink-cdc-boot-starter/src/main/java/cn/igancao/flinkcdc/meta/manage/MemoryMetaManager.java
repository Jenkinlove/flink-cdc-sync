package cn.igancao.flinkcdc.meta.manage;

import cn.igancao.flinkcdc.holder.FlinkJobBus;
import cn.igancao.flinkcdc.meta.FlinkJobIdentity;
import cn.igancao.flinkcdc.meta.LogPosition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存存储
 */
public class MemoryMetaManager extends AbstractMetaLifeCycle implements MetaManager{

    protected Map<FlinkJobIdentity, LogPosition> cursors;

    protected Map<String, FlinkJobIdentity> flinkJobs;


    @Override
    public void start() {
        super.start();
        cursors = new ConcurrentHashMap<>();
        flinkJobs = new ConcurrentHashMap<>();

        FlinkJobBus.setMetaManager(this);
    }

    @Override
    public void stop() {
        super.stop();
        cursors.clear();
        flinkJobs.clear();
    }

    @Override
    public LogPosition getCursor(FlinkJobIdentity flinkJobIdentity) {
        return cursors.get(flinkJobIdentity);
    }

    @Override
    public void updateCursor(FlinkJobIdentity flinkJobIdentity, LogPosition logPosition) {
        cursors.put(flinkJobIdentity, logPosition);
    }

    @Override
    public String getName() {
        return "memory";
    }
}

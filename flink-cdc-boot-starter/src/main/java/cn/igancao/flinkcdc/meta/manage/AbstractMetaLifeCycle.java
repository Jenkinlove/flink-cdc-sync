package cn.igancao.flinkcdc.meta.manage;

import cn.igancao.flinkcdc.common.exception.FlinkCDCException;

public class AbstractMetaLifeCycle implements MetaLifeCycle{

    /**
     * 运行标识
     */
    protected volatile  boolean running = false;

    @Override
    public void start() {
        if (running) {
            throw new FlinkCDCException(this.getClass().getName() + " has startup, don't repeat start");
        }
        running = true;
    }

    @Override
    public void stop() {
        if (!running) {
            throw new FlinkCDCException(this.getClass().getName() + " isn't start , please check");
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}

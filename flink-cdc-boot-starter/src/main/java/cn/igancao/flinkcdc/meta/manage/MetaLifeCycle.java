package cn.igancao.flinkcdc.meta.manage;

import java.io.Serializable;

public interface MetaLifeCycle extends Serializable {

    void start();

    void stop();

    boolean isRunning();
}

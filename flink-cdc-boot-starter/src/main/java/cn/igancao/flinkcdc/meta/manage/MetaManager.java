package cn.igancao.flinkcdc.meta.manage;

import cn.igancao.flinkcdc.meta.FlinkJobIdentity;
import cn.igancao.flinkcdc.meta.LogPosition;

import java.io.Serializable;

public interface MetaManager extends MetaLifeCycle, Serializable {

    /**
     * 获取游标
     */
    LogPosition getCursor(FlinkJobIdentity flinkJobIdentity);

    /**
     * 更新游标
     */
    void updateCursor(FlinkJobIdentity flinkJobIdentity, LogPosition logPosition);

    /**
     * 获取 meta 管理名称（唯一标识）
     */
    String getName();
}

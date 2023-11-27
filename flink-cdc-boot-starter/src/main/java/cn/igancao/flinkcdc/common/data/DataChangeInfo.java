package cn.igancao.flinkcdc.common.data;

import cn.igancao.flinkcdc.common.enums.EventType;
import lombok.Data;

/**
 * 数据变更对象
 */
@Data
public class DataChangeInfo {
    /**
     * 变更前数据
     */
    private String beforeData;
    /**
     * 变更后数据
     */
    private String afterData;
    /**
     * 变更类型 1新增 2修改 3删除
     */
    private EventType eventType;
    /**
     * binlog文件名
     */
    private String fileName;
    /**
     * binlog当前读取点位
     */
    private Integer filePos;
    /**
     * 数据库名
     */
    private String database;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 变更时间
     */
    private Long changeTime;
}

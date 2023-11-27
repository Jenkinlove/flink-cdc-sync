package cn.igancao.flinkcdc.properties;

import cn.igancao.flinkcdc.common.enums.StartupModeEnum;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import lombok.Data;

/**
 * flink job 配置信息
 */
@Data
public class FlinkJobProperties {

    /**
     * flink job name 必须唯一
     */
    private String name;

    private String hostname;

    private Integer port;

    private String databaseList;

    private String tableList;

    private String username;

    private String password;

    private String serverTimeZone = "GMT+8";

    /**
     * MySQL CDC使用者的启动模式
     * @see cn.igancao.flinkcdc.common.enums.StartupModeEnum
     */
    private String startupMode;

    private Long startupTimestampMillis;

    public StartupOptions getStartupOptions() {
        if (StartupModeEnum.INITIAL.name().equals(this.startupMode)) return StartupOptions.initial();
        if (StartupModeEnum.LATEST.name().equals(this.startupMode)) return StartupOptions.latest();
        if (StartupModeEnum.TIMESTAMP.name().equals(this.startupMode)) return StartupOptions.timestamp(startupTimestampMillis);
        return StartupOptions.latest();
    }

    public void setStartupMode(String startupMode) {
        StartupModeEnum.getStartupMode(startupMode);
        this.startupMode = startupMode;
    }
}

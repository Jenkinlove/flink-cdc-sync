package cn.igancao.flinkcdc.common.enums;

import cn.igancao.flinkcdc.common.exception.FlinkCDCException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MySQL CDC 使用时的启动模式
 */
@AllArgsConstructor
@Getter
public enum StartupModeEnum {

    INITIAL("初始化快照,即全量导入后增量导入(检测更新数据写入)"),
    LATEST("只进行增量导入(不读取历史变化)"),
    TIMESTAMP("指定时间戳进行数据导入(大于等于指定时间错读取数据)");

    private final String desc;

    public static StartupModeEnum getStartupMode(String startupMode) {
        for (StartupModeEnum startupModeEnum : StartupModeEnum.values()) {
            if (startupModeEnum.name().equalsIgnoreCase(startupMode)) {
                return startupModeEnum;
            }
        }
        throw new FlinkCDCException("不支持的CDC启动模式:" + startupMode);
    }
}

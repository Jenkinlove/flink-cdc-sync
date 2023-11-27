package cn.igancao.flinkcdc.common.enums;

import cn.igancao.flinkcdc.common.exception.FlinkCDCException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MetaModelEnum {

    FILE("file", "文件"),
    MEMORY("memory", "内存"),
    NACOS("nacos", "Nacos");

    private String value;

    private String desc;

    public static MetaModelEnum getMetaModelEnum(String value) {
        for (MetaModelEnum metaModelEnum : MetaModelEnum.values()) {
            if (metaModelEnum.getValue().equals(value)) {
                return metaModelEnum;
            }
        }
        throw new FlinkCDCException("cursor 记录方式不符合");
    }
}

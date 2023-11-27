package cn.igancao.flinkcdc.holder;

import cn.igancao.flinkcdc.common.exception.FlinkCDCException;
import cn.igancao.flinkcdc.properties.FlinkJobProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlinkJobPropertiesHolder {
    private static final Map<String, FlinkJobProperties> PROPERTIES_MAP = new ConcurrentHashMap<>();

    public static void registerProperties(FlinkJobProperties properties) {
        if (PROPERTIES_MAP.containsKey(properties.getName())) {
            throw new FlinkCDCException("flink job config " + properties.getName() + " is duplicate");
        }
        PROPERTIES_MAP.put(properties.getName(), properties);
    }

    public static Map<String, FlinkJobProperties> getPropertiesMap() {
        return PROPERTIES_MAP;
    }

    private FlinkJobPropertiesHolder() {
        throw new IllegalStateException("Utility class");
    }
}

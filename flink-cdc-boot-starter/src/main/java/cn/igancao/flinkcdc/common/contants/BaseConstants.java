package cn.igancao.flinkcdc.common.contants;

public class BaseConstants {

    public static final String PREFIX = "flink";

    public static final String ENABLE_PREFIX = PREFIX + ".enable";

    public static final String CONFIG_FILE = "flink-cdc-config.json";

    private BaseConstants() {
        throw new IllegalStateException("Utility class");
    }
}

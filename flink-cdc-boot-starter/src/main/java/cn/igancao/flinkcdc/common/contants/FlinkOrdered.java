package cn.igancao.flinkcdc.common.contants;

public class FlinkOrdered {

    //元数据管理
    public static final int ORDER_META_MANAGER = 10;

    //flink job 配置管理
    public static final int ORDER_LISTENER_PROPERTIES = 20;

    //flink job 管理
    public static final int ORDER_LISTENER = 30;

    private FlinkOrdered() {
        throw new IllegalStateException("Utility class");
    }
}

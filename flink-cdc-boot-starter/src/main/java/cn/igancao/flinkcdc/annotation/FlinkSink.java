package cn.igancao.flinkcdc.annotation;

import cn.igancao.flinkcdc.properties.FlinkJobProperties;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface FlinkSink {

    /**
     * flink job name
     * @see  FlinkJobProperties#getName()
     */
    String name();

    /**
     * 数据库名称
     * @see FlinkJobProperties#getDatabaseList()
     */
    String[] database() default {};

    /**
     * 表名称
     * @see FlinkJobProperties#getTableList()
     */
    String[] table() default {};

}

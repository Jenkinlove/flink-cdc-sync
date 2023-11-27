package cn.igancao.flinkcdc.meta;

import cn.igancao.flinkcdc.properties.FlinkProperties;
import lombok.Data;

import java.io.Serializable;

@Data
public class FlinkJobIdentity implements Serializable {

    private String applicationName;

    private String port;

    private String flinkJobName;

    public static FlinkJobIdentity generate(FlinkProperties.Meta meta, String flinkJobName) {
        String applicationName = meta.getApplicationName();
        String port = meta.getPort();

        FlinkJobIdentity flinkJobIdentity = new FlinkJobIdentity();
        flinkJobIdentity.setApplicationName(applicationName);
        flinkJobIdentity.setPort(port);
        flinkJobIdentity.setFlinkJobName(flinkJobName);
        return flinkJobIdentity;
    }

}

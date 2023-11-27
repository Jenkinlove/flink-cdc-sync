package cn.igancao.flinkcdc.meta;

import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import lombok.Data;

import java.io.Serializable;

/**
 * MySQL Binlog 读取位置
 *
 * @see StartupOptions#timestamp(long)
 */
@Data
public class LogPosition implements Serializable {

    private FlinkJobIdentity flinkJobIdentity;

    private Long startupTimestampMillis;
}

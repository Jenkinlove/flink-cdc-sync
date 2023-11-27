package cn.igancao.flinkcdcdemo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

@EqualsAndHashCode(callSuper = true)
@Data
@IndexName("teat_seata")
public class TeatSeata extends Base{

    @IndexField(fieldType = FieldType.LONG)
    @IndexId(type = IdType.CUSTOMIZE)
    private Long id;

    @IndexField(fieldType = FieldType.TEXT)
    private String name;
}

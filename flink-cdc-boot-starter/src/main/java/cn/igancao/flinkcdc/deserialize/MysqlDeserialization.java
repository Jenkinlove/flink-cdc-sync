package cn.igancao.flinkcdc.deserialize;

import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.enums.EventType;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Field;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Schema;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Struct;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Optional;

/**
 * mysql 消息读取自定义序列化
 */
public class MysqlDeserialization implements DebeziumDeserializationSchema<DataChangeInfo> {

    public static final String TS_MS = "ts_ms";
    public static final String BIN_FILE = "file";
    public static final String POS = "pos";
    public static final String BEFORE = "before";
    public static final String AFTER = "after";
    public static final String SOURCE = "source";

    /**
     * 反序列化数据，转为变更JSON对象
     */
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<DataChangeInfo> collector) throws Exception {
        String topic = sourceRecord.topic();
        String[] fields = topic.split("\\.");
        String database = fields[1];
        String tableName = fields[2];
        Struct struct = (Struct) sourceRecord.value();
        final Struct source = struct.getStruct(SOURCE);
        DataChangeInfo dataChangeInfo = new DataChangeInfo();
        dataChangeInfo.setBeforeData(getJsonObject(struct, BEFORE).toJSONString());
        dataChangeInfo.setAfterData(getJsonObject(struct, AFTER).toJSONString());
        //5.获取操作类型  CREATE UPDATE DELETE
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        String type = operation.toString().toUpperCase();
        EventType eventType = handleEventType(type);
        dataChangeInfo.setEventType(eventType);
        dataChangeInfo.setFileName(Optional.ofNullable(source.get(BIN_FILE)).map(Object::toString).orElse(""));
        dataChangeInfo.setFilePos(Optional.ofNullable(source.get(POS)).map(x -> Integer.parseInt(x.toString())).orElse(0));
        dataChangeInfo.setDatabase(database);
        dataChangeInfo.setTableName(tableName);
        Object value = source.get(TS_MS);
        dataChangeInfo.setChangeTime(Optional.ofNullable(value).map(x -> Long.parseLong(x.toString())).orElseGet(System::currentTimeMillis));
        //7.输出数据
        collector.collect(dataChangeInfo);
    }

    /**
     * 从原始数据获取出变更之前或之后的数据
     */
    private JSONObject getJsonObject(Struct struct, String before) {
        Struct element = struct.getStruct(before);
        JSONObject jsonObject = new JSONObject();
        if (element != null) {
            Schema afterSchema = element.schema();
            List<Field> fieldList = afterSchema.fields();
            for (Field field : fieldList) {
                Object afterValue = element.get(field);
                jsonObject.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.name()), afterValue);
            }
        }
        return jsonObject;
    }

    /**
     * 转化操作类型
     */
    private EventType handleEventType(String type) {
        EventType eventType = null;
        if (type.equals(EventType.CREATE.getName()) || type.equals(EventType.READ.getName())) {
            eventType = EventType.CREATE;
        } else if (type.equals(EventType.UPDATE.getName())) {
            eventType = EventType.UPDATE;
        } else if (type.equals(EventType.DELETE.getName())) {
            eventType = EventType.DELETE;
        }
        return eventType;
    }

    @Override
    public TypeInformation<DataChangeInfo> getProducedType() {
        return TypeInformation.of(DataChangeInfo.class);
    }
}

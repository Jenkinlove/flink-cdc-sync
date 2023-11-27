package cn.igancao.flinkcdc.holder.filter;

import cn.igancao.flinkcdc.annotation.FlinkSink;
import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;
import org.springframework.util.ObjectUtils;

public class FlinkJobSinkTableFilter extends FlinkJobSinkFilter {
    @Override
    boolean filter(FlinkJobSink sink, DataChangeInfo dataChangeInfo) {
        FlinkSink annotation = sink.getClass().getAnnotation(FlinkSink.class);
        if (annotation == null) return false;

        String[] tableNames = annotation.table();
        if (ObjectUtils.isEmpty(tableNames)) return doNextFilter(sink, dataChangeInfo);

        String dataChangeInfoDatabase = dataChangeInfo.getDatabase();
        String dataChangeInfoTableName = dataChangeInfo.getTableName();

        // 过滤数据库
        for (String tableName : tableNames) {
            String[] split = tableName.split("\\.");

            if (dataChangeInfoDatabase.equals(split[0]) && ("*".equals(split[1]) || dataChangeInfoTableName.equals(split[1]))) {
                    return doNextFilter(sink, dataChangeInfo);
            }

        }
        return false;
    }
}

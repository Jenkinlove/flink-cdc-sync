package cn.igancao.flinkcdc.holder.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.igancao.flinkcdc.annotation.FlinkSink;
import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;

/**
 * 数据库校验
 */
public class FlinkJobSinkDatabaseFilter extends FlinkJobSinkFilter{

    @Override
    public boolean filter(FlinkJobSink sink, DataChangeInfo dataChangeInfo) {
        FlinkSink annotation = sink.getClass().getAnnotation(FlinkSink.class);
        if (annotation == null) {
            return false;
        }
        String[] databases = annotation.database();
        if (ObjectUtil.isEmpty(databases)) {
            return doNextFilter(sink, dataChangeInfo);
        }
        for (String database : databases) {
            if (database.equals(dataChangeInfo.getDatabase())) {
                return doNextFilter(sink, dataChangeInfo);
            }
        }
        return false;
    }
}

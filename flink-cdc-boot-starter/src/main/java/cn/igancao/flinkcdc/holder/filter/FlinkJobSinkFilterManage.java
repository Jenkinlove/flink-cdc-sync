package cn.igancao.flinkcdc.holder.filter;

import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;

public class FlinkJobSinkFilterManage {
    private static final FlinkJobSinkFilter filter;
    static {
        filter = new FlinkJobSinkDatabaseFilter();
        filter.setFilter(new FlinkJobSinkTableFilter());
    }

    public static boolean filter(FlinkJobSink sink, DataChangeInfo dataChangeInfo) {
        return filter.filter(sink, dataChangeInfo);
    }
}

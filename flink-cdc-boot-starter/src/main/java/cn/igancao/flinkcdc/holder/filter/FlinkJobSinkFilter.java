package cn.igancao.flinkcdc.holder.filter;

import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;

/**
 * flink sink 是否接收数据变更消息过滤器
 */
public abstract class FlinkJobSinkFilter {

    private FlinkJobSinkFilter nextFilter;

    public void setFilter(FlinkJobSinkFilter filter) {
        this.nextFilter = filter;
    }

    public FlinkJobSinkFilter getNextFilter() {
        return nextFilter;
    }

    public boolean doNextFilter(FlinkJobSink sink, DataChangeInfo dataChangeInfo) {
        return nextFilter == null || nextFilter.filter(sink, dataChangeInfo);
    }

    abstract boolean filter(FlinkJobSink sink, DataChangeInfo dataChangeInfo);
}

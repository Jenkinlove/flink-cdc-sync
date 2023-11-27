package cn.igancao.flinkcdc.common.service;

import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public interface FlinkJobSink extends SinkFunction<DataChangeInfo> {
    @Override
    default void invoke(DataChangeInfo value, Context context) throws Exception {

    }

    default void insert(DataChangeInfo value, Context context) throws Exception {

    }

    default void update(DataChangeInfo value, Context context) throws Exception {

    }

    default void delete(DataChangeInfo value, Context context) throws Exception {

    }

    default void handleError(DataChangeInfo value, Context context, Throwable throwable) {

    }
}

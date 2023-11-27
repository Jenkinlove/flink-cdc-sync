package cn.igancao.flinkcdc.holder;

import cn.hutool.core.collection.CollUtil;
import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.common.enums.EventType;
import cn.igancao.flinkcdc.common.exception.FlinkCDCException;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;
import cn.igancao.flinkcdc.holder.filter.FlinkJobSinkFilterManage;
import cn.igancao.flinkcdc.meta.FlinkJobIdentity;
import cn.igancao.flinkcdc.meta.LogPosition;
import cn.igancao.flinkcdc.meta.manage.MetaManager;
import cn.igancao.flinkcdc.properties.FlinkJobProperties;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.List;

@Slf4j
public class FlinkJobBus {
    private static MetaManager metaManager;

    /**
     * 创建 flink 临时任务
     */
    public static void createTempFlinkJob(String flinkJobName) {
        if (!FlinkJobPropertiesHolder.getPropertiesMap().containsKey(flinkJobName)) {
            throw new FlinkCDCException("The system is not configured for this Flink job!");
        }
        FlinkJobProperties flinkJobProperties = FlinkJobPropertiesHolder.getPropertiesMap().get(flinkJobName);


    }

    /**
     * 通知
     */
    public static void post(FlinkJobIdentity flinkJobIdentity, DataChangeInfo dataChangeInfo, SinkFunction.Context context) {
        List<FlinkJobSink> sinks = FlinkSinkHolder.getSink(flinkJobIdentity.getFlinkJobName());
        if (CollUtil.isEmpty(sinks)) {
            return;
        }
        for (FlinkJobSink sink : sinks) {
            if (FlinkJobSinkFilterManage.filter(sink, dataChangeInfo)) {
                invoke(sink, dataChangeInfo, context);
            }
        }

        updateCursor(dataChangeInfo, flinkJobIdentity);
    }

    public static void invoke(FlinkJobSink sink, DataChangeInfo dataChangeInfo, SinkFunction.Context context) {
        try {
            EventType eventType = dataChangeInfo.getEventType();
            sink.invoke(dataChangeInfo, context);

            if (EventType.CREATE.equals(eventType)) {
                sink.insert(dataChangeInfo, context);
            } else if (EventType.UPDATE.equals(eventType)) {
                sink.update(dataChangeInfo, context);
            } else if (EventType.DELETE.equals(eventType)) {
                sink.delete(dataChangeInfo, context);
            }
        } catch (Exception e) {
            log.error("sink invoke error, sink:[{}], dataChangeInfo:[{}], error:[{}]", sink, dataChangeInfo, Throwables.getStackTraceAsString(e));
            sink.handleError(dataChangeInfo, context, e);
        }
    }

    public static void updateCursor(DataChangeInfo dataChangeInfo, FlinkJobIdentity flinkJobIdentity) {
        LogPosition logPosition = new LogPosition();
        logPosition.setFlinkJobIdentity(flinkJobIdentity);
        logPosition.setStartupTimestampMillis(dataChangeInfo.getChangeTime());
        metaManager.updateCursor(flinkJobIdentity, logPosition);
        log.info("flink job:[{}], update on [{}]", flinkJobIdentity.getFlinkJobName(), dataChangeInfo.getChangeTime());
    }

    public static void setMetaManager(MetaManager metaManager) {
        FlinkJobBus.metaManager = metaManager;
    }

    public static MetaManager getMetaManager() {
        return metaManager;
    }

    private FlinkJobBus() {
        throw new IllegalStateException("Utility class");
    }
}

package cn.igancao.flinkcdc.proxy;

import cn.igancao.flinkcdc.common.data.DataChangeInfo;
import cn.igancao.flinkcdc.holder.FlinkJobBus;
import cn.igancao.flinkcdc.meta.FlinkJobIdentity;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * FlinkSink 代理类
 */
@Slf4j
public class FlinkSinkProxy implements InvocationHandler, Serializable {

    private final FlinkJobIdentity flinkJobIdentity;

    public FlinkSinkProxy(FlinkJobIdentity flinkJobIdentity) {
        this.flinkJobIdentity = flinkJobIdentity;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DataChangeInfo dataChangeInfo = null;
        try {
            if (!"invoke".equals(method.getName())) {
                return null;
            }
            dataChangeInfo = (DataChangeInfo) args[0];
            SinkFunction.Context context = (SinkFunction.Context) args[1];
            FlinkJobBus.post(flinkJobIdentity, dataChangeInfo, context);
            log.info("flinkJobIdentity:{}, dataChangeInfo:{}", flinkJobIdentity, dataChangeInfo);
        } catch (Exception e) {
            log.error("flinkJobIdentity:{}, dataChangeInfo:{}, error:{}", flinkJobIdentity, dataChangeInfo, Throwables.getStackTraceAsString(e));
        }
        return null;
    }
}

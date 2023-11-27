package cn.igancao.flinkcdc.holder;

import cn.igancao.flinkcdc.annotation.FlinkSink;
import cn.igancao.flinkcdc.common.service.FlinkJobSink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlinkSinkHolder {
    private static final Map<String, List<FlinkJobSink>> SINK_MAP = new ConcurrentHashMap<>();

    public static void registerSink(FlinkJobSink jobSink, FlinkSink flinkSink) {
        String name = flinkSink.name();
        List<FlinkJobSink> sinkList = SINK_MAP.getOrDefault(name, new ArrayList<>());
        sinkList.add(jobSink);
        SINK_MAP.put(name, sinkList);
    }

    public static List<FlinkJobSink> getSink(String flinkJobName) {
        return SINK_MAP.get(flinkJobName);
    }

    private FlinkSinkHolder() {
        throw new IllegalStateException("Utility class");
    }
}

package code.flink.stream;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @describe:
 * @author: shuang.peng
 * @date: 2020/08/07
 */
public class InitUtils {

    public static StreamExecutionEnvironment getStreamingEnvironment(){

        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置使用eventtime ，默认是processtime
        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // 设置并行度为1，默认是当前机器的cpu数量
        environment.setParallelism(1);
        ExecutionConfig executionConfig = environment.getConfig();
        executionConfig.setAutoWatermarkInterval(200000);
        return environment;
    }

}

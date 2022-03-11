package code.flink.state;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @Description 统计每个key的计数，并且每一分钟发出一个没有更新key的key/count对。
 * @Date 2021/3/24
 * @Created by shuang.peng
 */
public class ProcessFunctionMain {

    private static String HOST = "localhost";
    private static int PORT = 9999;

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> stream = env.socketTextStream(HOST, PORT);

        SingleOutputStreamOperator<Tuple2<String, Integer>> operator = stream
                .flatMap(new LineSplitFunction())
                .keyBy(0)
                .process(new ProcessFunction());
        operator.print();
        env.execute("test process function");
    }
}


class LineSplitFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {

    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
//        String[] split = value.split(",");
//        System.out.println(Arrays.toString(split));
        out.collect(new Tuple2<>(value, 1));
    }
}

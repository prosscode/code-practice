package code.flink.state;


import com.sun.xml.internal.xsom.XSUnionSimpleType;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;

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
                .process(new TestProcessFunction());
        operator.print();
        env.execute("test processfucntion");
    }
}


class LineSplitFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {

    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
//        String[] split = value.split(",");
//        System.out.println(Arrays.toString(split));
        out.collect(new Tuple2<String,Integer>(value, 1));
    }
}


class TestProcessFunction extends KeyedProcessFunction<Tuple, Tuple2<String, Integer>, Tuple2<String, Integer>> {

    /** The state that is maintained by this process function */
    private ValueState<CountWithTimestamp> valueState;
    private MapState<String,Integer> mapState;

    @Override
    public void open(Configuration parameters) throws Exception {

        // 初始化ValueState
        ValueStateDescriptor<CountWithTimestamp> stateDescriptor =
                new ValueStateDescriptor("valueState",
                        TypeInformation.of(new TypeHint<CountWithTimestamp>() {}));
        valueState = getRuntimeContext().getState(stateDescriptor);

        // 初始化mapState
        MapStateDescriptor<String, Integer> mapStateDescriptor = new MapStateDescriptor<String,Integer>("mapState",
                TypeInformation.of(new TypeHint<String>() {}),
                TypeInformation.of(new TypeHint<Integer>() {}));
        mapState = getRuntimeContext().getMapState(mapStateDescriptor);
    }

    @Override
    public void processElement(
            Tuple2<String, Integer> value,
            Context ctx,
            Collector<Tuple2<String, Integer>> out) throws Exception {

        System.out.println("input:"+value.f0+","+value.f1);

        CountWithTimestamp current = valueState.value();
        if (current == null) {
            current = new CountWithTimestamp();
            current.key = value.f0;
        }
        // value state
        current.count++;
        valueState.update(current);

        // map state
        String mapKey = current.key;
        int mapValue = current.count + 100;
        mapState.put(mapKey,mapValue);

        // output state
        System.out.println(valueState.value().toString());
        System.out.println(mapState.get(valueState.value().key));
        out.collect(new Tuple2<String, Integer>(mapKey, mapValue));
        // schedule the next timer 60 seconds from the current event time
//        ctx.timerService().registerEventTimeTimer(current.lastModified + 10000);
    }

    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<String, Integer>> out) throws Exception {
        // get the state for the key that scheduled the timer
        CountWithTimestamp result = valueState.value();
        int mapValue = mapState.get(valueState.value().key);
        // check if this is an outdated timer or the latest timer
        if (timestamp == result.lastModified + 10000) {
            // emit the state on timeout
            out.collect(new Tuple2<String, Integer>(result.key, mapValue));
        }
    }

}


class CountWithTimestamp {

    public String key;
    public int count;
    public long lastModified;

    public CountWithTimestamp() {
        this.key = "";
        this.count = 0;
        this.lastModified = System.currentTimeMillis() / 1000;
    }

    @Override
    public String toString() {
        return "CountWithTimestamp{" +
                "key='" + key + '\'' +
                ", count=" + count +
                ", lastModified=" + lastModified +
                '}';
    }
}
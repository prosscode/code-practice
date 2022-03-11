package code.flink.state;

import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @date 2022/3/11
 * @created by shuang.peng
 * @description ProcessFunction
 */
public class ProcessFunction extends KeyedProcessFunction<Tuple, Tuple2<String, Integer>, Tuple2<String, Integer>> {

    /**
     * The state that is maintained by this process function
     */
    private ValueState<ValueStateProcess> valueState;
    private MapState<String, Integer> mapState;
    private ReducingState<Integer> reducingState;
    private AggregatingState<String, Integer> aggregationState;


    @Override
    public void open(Configuration parameters) throws Exception {

        // 初始化ValueState
        ValueStateDescriptor<ValueStateProcess> stateDescriptor =
                new ValueStateDescriptor("valueState",
                        TypeInformation.of(new TypeHint<ValueStateProcess>() {
                        }));
        valueState = getRuntimeContext().getState(stateDescriptor);

        // 初始化mapState
        MapStateDescriptor<String, Integer> mapStateDescriptor = new MapStateDescriptor<String, Integer>("mapState",
                TypeInformation.of(new TypeHint<String>() {
                }),
                TypeInformation.of(new TypeHint<Integer>() {
                }));
        mapState = getRuntimeContext().getMapState(mapStateDescriptor);

        // 初始化reducingState
        ReducingStateDescriptor reducingStateDescriptor = new ReducingStateDescriptor<>("reducingState",
                new ReduceStateProcess(),
                Integer.class);
        reducingState = getRuntimeContext().getReducingState(reducingStateDescriptor);

        // 初始化aggregationState
        AggregatingStateDescriptor aggregatingState = new AggregatingStateDescriptor<Double, Tuple2<String, Integer>, Double>("aggregatingState",
                new AggregatingStateProcess(),
                TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {
                }));
        aggregationState = getRuntimeContext().getAggregatingState(aggregatingState);

    }

    @Override
    public void processElement(
            Tuple2<String, Integer> value,
            Context ctx,
            Collector<Tuple2<String, Integer>> out) throws Exception {

        System.out.println("input:" + value.f0 + "," + value.f1);

        ValueStateProcess current = valueState.value();
        if (current == null) {
            current = new ValueStateProcess();
            current.key = value.f0;
        }
        // value state
        current.count++;
        valueState.update(current);

        // map state
        String mapKey = current.key;
        int mapValue = current.count + 100;
        mapState.put(mapKey, mapValue);

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
        ValueStateProcess result = valueState.value();
        int mapValue = mapState.get(valueState.value().key);
        // check if this is an outdated timer or the latest timer
        if (timestamp == result.lastModified + 10000) {
            // emit the state on timeout
            out.collect(new Tuple2<String, Integer>(result.key, mapValue));
        }
    }

}

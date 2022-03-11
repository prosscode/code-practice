package code.flink.state;


import org.apache.flink.api.common.functions.AggregateFunction;

/**
 * @date 2022/3/11
 * @created by shuang.peng
 * @description AggregatingStateProcess
 */
public class AggregatingStateProcess implements AggregateFunction {


    @Override
    public Object createAccumulator() {
        return null;
    }

    @Override
    public Object add(Object value, Object accumulator) {
        return null;
    }

    @Override
    public Object getResult(Object accumulator) {
        return null;
    }

    @Override
    public Object merge(Object a, Object b) {
        return null;
    }
}

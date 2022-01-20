package code.flink.table.submit.operands;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.types.Row;

import java.util.Comparator;

import static code.flink.table.submit.operands.ColumnIndexConstants.*;


public abstract class WindowCompareAggFn implements AggregateFunction<Row, Tuple3<String, String, Long>, Row>, Comparator<Tuple3<String, String, Long>> {

    private final String metricName;
   // private final Comparator<Tuple3<String, String, Long>> comparable;

    public WindowCompareAggFn(String metricName){//, Function<? super Tuple3<String, String, Long>, ? extends Long> comparable) {
        this.metricName = metricName;
     //   this.comparable = Comparator.comparing(comparable);
    }

    @Override
    public Tuple3<String, String, Long> createAccumulator() {
        return null;
    }

    @Override
    public Tuple3<String, String, Long> add(Row row, Tuple3<String, String, Long> acc) {
        String id = (String) row.getField(IdIndex);
        String val = (String) row.getField(ValueIndex);
        Long ts = (Long) row.getField(TsIndex);
        Tuple3<String, String, Long> tuple = new Tuple3(id, val, ts);
        if (acc == null || this.compare(tuple, acc) > 0) {
            acc = new Tuple3<>(id, val, ts);
        }
        return acc;
    }

    @Override
    public Row getResult(Tuple3<String, String, Long> acc) {
        String id = acc.f0;
        String val = acc.f1;
        Long ts = acc.f2;

        return Row.of(String.format("%s_%d", id, ts), metricName, val);
    }

    @Override
    public Tuple3<String, String, Long> merge(Tuple3<String, String, Long> a, Tuple3<String, String, Long> b) {
        if (a == null) return b;
        if (b == null) return a;
        return this.compare(a, b) > 0 ? a : b;
    }


}

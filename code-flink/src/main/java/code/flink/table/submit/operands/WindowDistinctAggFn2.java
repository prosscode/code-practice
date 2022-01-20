package code.flink.table.submit.operands;

import code.flink.table.submit.Utils;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.types.Row;
import org.roaringbitmap.RoaringBitmap;
import scala.util.hashing.MurmurHash3$;

import java.util.HashMap;
import java.util.Map;

import static code.flink.table.submit.operands.ColumnIndexConstants.*;


public class WindowDistinctAggFn2 implements AggregateFunction<Row, Tuple4<String, Long, RoaringBitmap,
        Map<Tuple2<String,Integer>,Tuple2<Long,Long>>>, Row> {

    private final String metricName;//, offsetName;
    private final long intervalSec;

    public WindowDistinctAggFn2(String metricName, long intervalSec) {
        this.metricName = metricName;
       // this.offsetName = metricName.substring(0,metricName.length()-3) + "oft";
        this.intervalSec = intervalSec;
    }

    @Override
    public Tuple4<String, Long, RoaringBitmap, Map<Tuple2<String,Integer>,Tuple2<Long,Long>>> createAccumulator() {
        return null;
    }

    @Override
    public Tuple4<String, Long, RoaringBitmap, Map<Tuple2<String,Integer>,Tuple2<Long,Long>>> add(Row value,
                                                                                                  Tuple4<String, Long, RoaringBitmap, Map<Tuple2<String,Integer>,Tuple2<Long,Long>>> accumulator) {
        // 1) add value
        long windowStart = (Long) value.getField(TsIndex) / intervalSec * intervalSec;
        if (accumulator == null) {
            accumulator = new Tuple4<>((String) value.getField(IdIndex), windowStart,
                    new RoaringBitmap(), new HashMap<>());
        }
        String v = (String) value.getField(ValueIndex);
        int hashValue = MurmurHash3$.MODULE$.bytesHash(v.getBytes(), -1);
        accumulator.f2.add(hashValue);

        // 2) refresh max/min kafka offset
        Utils.RefreshKafkaOffset((Row)value.getField(3), accumulator.f3);
        return accumulator;
    }

    @Override //row key: id_ts;  qf: ts; value: value
    public Row getResult(Tuple4<String, Long, RoaringBitmap, Map<Tuple2<String,Integer>,Tuple2<Long,Long>>> acc) {
        String id = acc.f0;
        Long ts = acc.f1;
        String value = acc.f2.getCardinality() == 1 ? acc.f2.toString() : Utils.Serialize(acc.f2);
        return Row.of(String.format("%s_%d", id, ts),
                metricName, value, acc.f3.toString());
    }

    @Override
    public Tuple4<String, Long, RoaringBitmap, Map<Tuple2<String,Integer>,Tuple2<Long,Long>>> merge(Tuple4<String, Long, RoaringBitmap, Map<Tuple2<String,Integer>,Tuple2<Long,Long>>> a, Tuple4<String, Long, RoaringBitmap, Map<Tuple2<String,Integer>,Tuple2<Long,Long>>> b) {
        a.f2.and(b.f2);
        Utils.MergeKafkaOffset(a.f3,b.f3);
        return a;
    }
}

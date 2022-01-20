package code.flink.table.submit.operands;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.types.Row;
import org.roaringbitmap.RoaringBitmap;
import scala.util.hashing.MurmurHash3$;

import static code.flink.table.submit.operands.ColumnIndexConstants.*;

public class WindowDistinctAggFn implements AggregateFunction<Row, Tuple3<String, Long, RoaringBitmap>, Row> {

    private final String metricName;
    private final long intervalSec;

    public WindowDistinctAggFn(String metricName, long intervalSec) {
        this.metricName = metricName;
        this.intervalSec = intervalSec;
    }

    @Override
    public Tuple3<String, Long, RoaringBitmap> createAccumulator() {
        return null;
    }

    @Override
    public Tuple3<String, Long, RoaringBitmap> add(Row value, Tuple3<String, Long, RoaringBitmap> accumulator) {
        long windowStart = (Long) value.getField(TsIndex) / intervalSec * intervalSec;
        if (accumulator == null) {
            accumulator = new Tuple3<>((String) value.getField(IdIndex), windowStart,
                    new RoaringBitmap());
        }
        if (windowStart != accumulator.f1) {
            throw new NullPointerException(String.format("data not in the same window:%s", accumulator.toString()));
        }
        String v = (String) value.getField(ValueIndex);
        int hashValue = MurmurHash3$.MODULE$.bytesHash(v.getBytes(), -1);
        accumulator.f2.add(hashValue);
        //logger.info(String.format("hash value: %s =>%d", v, hashValue));
        //MurmurHash3.getInstance().hash(new ByteArrayHashKey(v.getBytes(),0,v.getBytes().length),-1)
        return accumulator;
    }

    @Override //row key: id_ts;  qf: ts; value: value
    public Row getResult(Tuple3<String, Long, RoaringBitmap> acc) {
        String id = acc.f0;
        Long ts = acc.f1;
//        String value = acc.f2.getCardinality() == 1 ? acc.f2.toString() : Utils.Serialize(acc.f2);
        String value = "";
        return Row.of(String.format("%s_%d", id, ts),
                metricName, value);
    }

    @Override
    public Tuple3<String, Long, RoaringBitmap> merge(Tuple3<String, Long, RoaringBitmap> a, Tuple3<String, Long, RoaringBitmap> b) {
        a.f2.and(b.f2);
        return a;
    }

//    public static void main(String[] args){
//        String token ="qiaodan";
//        System.out.println(MurmurHash3$.MODULE$.bytesHash(token.getBytes(), -1));
//        System.out.println(MurmurHash3.getInstance().hash(new ByteArrayHashKey(token.getBytes(),0,token.getBytes().length),-1));
//    }
}

package code.flink.table.submit.operands;


import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static code.flink.table.submit.operands.ColumnIndexConstants.IdIndex;
import static code.flink.table.submit.operands.ColumnIndexConstants.TsIndex;


//accType : {id, acc, ts}
public final class WindowCountAggFn implements AggregateFunction<Row, Tuple3<String, Long, Long>, Row> {
    private static final Logger logger = LoggerFactory.getLogger(WindowCountAggFn.class);

    private final String metricName;
    private final long intervalSec;

    public WindowCountAggFn(String metricName, long intervalSec) {
        this.metricName = metricName;
        this.intervalSec = intervalSec;
    }

    @Override
    public Tuple3<String,Long,Long> createAccumulator() {
        return null;
    }

    @Override
    public Tuple3<String,Long,Long> add(Row value, Tuple3<String,Long,Long> acc) {
        String id = (String) value.getField(IdIndex);
        Long windowStart = (Long) value.getField(TsIndex)/intervalSec*intervalSec;
        if(acc ==null){
            acc = new Tuple3<>(id,1L,windowStart);
        } else {
            acc.f1 = acc.f1+1L;
        }
        return acc;
    }

    @Override
    public Row getResult(Tuple3<String,Long,Long> acc) {
        String id = acc.f0;
        Long cnt = acc.f1;
        Long ts = acc.f2;
//        try {
//            TimeUnit.SECONDS.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return Row.of(String.format("%s_%d",id, ts),metricName,cnt.toString());
    }

    @Override
    public Tuple3<String,Long,Long> merge(Tuple3<String,Long,Long> a, Tuple3<String,Long,Long> b) {
        if(a==null) return b;
        if(b==null) return a;
        a.f1 += b.f1;
        return a;
    }
}

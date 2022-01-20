package code.flink.table.submit.operands;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.types.Row;
import org.roaringbitmap.RoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.util.hashing.MurmurHash3$;

import static code.flink.table.submit.operands.ColumnIndexConstants.*;

public class BitmapMapFunction implements MapFunction<Row, Row>, ResultTypeQueryable<Row> {
    private final Logger logger = LoggerFactory.getLogger(BitmapMapFunction.class);
    private final String metricName;

    public BitmapMapFunction(String metricName) {
        this.metricName = metricName;
    }

    @Override // {id, value, ts} => id_ts, ts_value, set(value)
    public Row map(Row value) {
        String v = (String) value.getField(ValueIndex);
        RoaringBitmap bitmap = new RoaringBitmap();
        int hashValue = MurmurHash3$.MODULE$.bytesHash(v.getBytes(), -1);
        bitmap.add(hashValue);
        Long ts = (Long) value.getField(TsIndex);
        //rowkey, cf, qf, value, version
        Row r= Row.of(String.format("%s_%d_%s", value.getField(IdIndex).toString(), ts, v),
                metricName,
                bitmap.toString());
        logger.info(String.format("side output bitmap:%s",r.toString()));
        return r;
    }

    @Override
    public TypeInformation<Row> getProducedType() { //{row key,  qf, value}
        return Types.ROW(Types.STRING, Types.STRING, Types.STRING);
    }
}

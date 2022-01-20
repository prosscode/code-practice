package code.flink.table.submit.operands;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static code.flink.table.submit.operands.ColumnIndexConstants.*;

public class CountMapFn implements MapFunction<Row, Row>, ResultTypeQueryable<Row> {
    private static final Logger logger = LoggerFactory.getLogger(CountMapFn.class);

    private final String metricName;

    public CountMapFn(String metricName) {
        this.metricName = metricName;
    }

    @Override
    public Row map(Row value)  {
        String v = (String) value.getField(ValueIndex);
        Long ts = (Long) value.getField(TsIndex);
        //Long windowStart =  ts/ intervalSec * intervalSec;
        //rowkey, qf, value
        Row r = Row.of(String.format("%s_%d_%s", value.getField(IdIndex).toString(), ts, v),
                metricName, "1");
        logger.info(String.format("side output count:%s",r.toString()));
        return r;
    }

    @Override
    public TypeInformation<Row> getProducedType() { //{row key, qf, value}
        return Types.ROW(Types.STRING, Types.STRING, Types.STRING);
    }
}

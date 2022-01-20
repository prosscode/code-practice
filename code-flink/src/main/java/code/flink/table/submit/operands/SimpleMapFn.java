package code.flink.table.submit.operands;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.types.Row;

import static code.flink.table.submit.operands.ColumnIndexConstants.*;


public class SimpleMapFn implements MapFunction<Row, Row>, ResultTypeQueryable<Row> {

    private final String metricName;
   // private final Function<String,String> fn;

    public SimpleMapFn(String metricName){//}, Function<String, String> fn) {
        this.metricName = metricName;
      //  this.fn = fn;
    }

    @Override
    public Row map(Row value) {
        String v = (String) value.getField(ValueIndex);
        Long ts =(Long) value.getField(TsIndex);
        //rowkey, qf, value
        return Row.of(String.format("%s_%d_%s",value.getField(IdIndex).toString(),ts,v),
                metricName,
                v);
               // fn.apply(v));
    }

    @Override
    public TypeInformation<Row> getProducedType() { //{row key, qf, value}
        return Types.ROW(Types.STRING, Types.STRING, Types.STRING);
    }
}

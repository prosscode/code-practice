package code.flink.table.udf;

import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;

/**
 * @date 2022/1/20
 * @created by shuang.peng
 * @description GeoHash
 */
public class GeoHash extends ScalarFunction {

    @Override
    public void open(FunctionContext context) throws Exception {
        super.open(context);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

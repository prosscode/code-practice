package code.flink.table.submit.operands;

import org.apache.flink.api.java.tuple.Tuple3;

public class WindowLastAggFn extends WindowCompareAggFn {
    public WindowLastAggFn(String metricName) {
        super(metricName);
    }

    @Override
    public int compare(Tuple3<String, String, Long> o1, Tuple3<String, String, Long> o2) {
        return o1.f2.compareTo(o2.f2);
    }
}

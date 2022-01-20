package code.flink.table.submit.operands;

import code.flink.table.submit.conf.MetricCategory;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.types.Row;


public class AggregatorStrategies {
    public static SingleOutputStreamOperator<Row> AggByCategory(WindowedStream<Row,String, TimeWindow> windowedStream, MetricCategory category, long intervalSec, String metricName){
        switch (category){
            case Distinct:
                return windowedStream.aggregate(new WindowDistinctAggFn(metricName, intervalSec));
            case Count:
                return windowedStream.aggregate(new WindowCountAggFn(metricName, intervalSec));
            /*case Max:
                return windowedStream.aggregate(new WindowCompareAggFn(metricName, (Tuple3<String, String, Long> a) -> a.f1));

            case Min:
                return windowedStream.aggregate(new WindowCompareAggFn(metricName,
                        (Tuple3<String, String, Long> a,Tuple3<String, String, Long> b) -> b.f1.compareTo(a.f1)));*/
            case Last:
                return windowedStream.aggregate(new WindowLastAggFn(metricName));//, (Tuple3<String, String, Long> a) -> a.f2));
            default:
                throw new IllegalArgumentException("MetricCategory can't be Unknown");
        }
    }

    public static SingleOutputStreamOperator<Row> AggByCategory2(WindowedStream<Row,String, TimeWindow> windowedStream, MetricCategory category,
                                                                 long intervalSec, String metricName) {
        switch (category) {
            case Distinct:
                return windowedStream.aggregate(new WindowDistinctAggFn2(metricName, intervalSec));
            default:
                throw new IllegalArgumentException("MetricCategory "+category +" is not supported");
        }
    }

    public static SingleOutputStreamOperator<Row> AggByCategory(DataStream<Row> sideOutputStream, MetricCategory category, long intervalSec, String metricName){
        switch (category){
            case Distinct:
                return sideOutputStream.map(new BitmapMapFunction(metricName));
            case Count:
                return sideOutputStream.map(new CountMapFn( metricName));
            case Max:
            case Min:
            case Last:
                return sideOutputStream.map(new SimpleMapFn(metricName));//, value->value));
            default:
                throw new IllegalArgumentException("MetricCategory can't be Unknown");
        }
    }
}

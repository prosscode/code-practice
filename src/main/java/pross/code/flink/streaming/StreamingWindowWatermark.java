package pross.code.flink.streaming;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static pross.code.flink.streaming.InitUtils.getStreamingEnvironment;

/**
 * @describe: 测试eventime+watermark案例
 * @author: 彭爽 pross.peng
 * @date: 2020/08/07
 */
public class StreamingWindowWatermark {

    public static void main(String[] args) throws Exception {
        // 定义端口号
        int port = 9000;
        // 初始化环境
        StreamExecutionEnvironment environment = getStreamingEnvironment();
        // 连接socket，获取输入的数据
        DataStreamSource<String> streamSource = environment.socketTextStream("localhost", port, "\n");

        // 解析从端口输入的数据
        SingleOutputStreamOperator<Tuple2<String,Long>> inputMap = streamSource.map(new MapFunction<String, Tuple2<String,Long>>() {

            @Override
            public Tuple2 map(String value) throws Exception {
                String[] arr = value.split(",");
                return new Tuple2<>(arr[0], Long.parseLong(arr[1]));
            }
        });


        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 抽取timestamp 生成watermark
        SingleOutputStreamOperator<Tuple2<String,Long>> timestampsAndWatermarks = inputMap.assignTimestampsAndWatermarks(
                new AssignerWithPeriodicWatermarks<Tuple2<String,Long>>() {

            Long currentMaxTimestamp = 0L;
            // 最大允许的乱序时间是10s
            final Long maxOutOfOrderness = 10000L;

            @Override
            public long extractTimestamp(Tuple2<String,Long> element, long l) {
                Tuple2<String, Long> elements = (Tuple2<String, Long>) element;
                Long timestamp = elements.f1;
                currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
                System.out.println("key:" + elements.f0
                        + ",eventtime:「" + elements.f1 + "|" + sdf.format(elements.f1)
                        + "」,currentmaxtimestamp:「" + currentMaxTimestamp + "|" + sdf.format(currentMaxTimestamp)
                        + "」,watermark:「" + getCurrentWatermark().getTimestamp() + "|" + sdf.format(getCurrentWatermark().getTimestamp()) + "」");
                return timestamp;
            }

            @Nullable
            @Override
            public Watermark getCurrentWatermark() {
                return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
            }
        });

        // 保存被丢弃的数据
        OutputTag<Tuple2<String,Long>> outputTag = new OutputTag<Tuple2<String,Long>>("OutputTag");

        //分组 聚合 加窗口 并排序
        DataStream<String> apply = timestampsAndWatermarks.keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(15)))
                // 允许数据延迟5秒
                .allowedLateness(Time.seconds(5))
                // 保存被丢弃的数据
                .sideOutputLateData(outputTag)
                // 全量聚合，可排序后输出
                .apply(new WindowFunction<Tuple2<String,Long>, String, Tuple, TimeWindow>() {

                    @Override
                    public void apply(Tuple tuple, TimeWindow timeWindow, Iterable<Tuple2<String, Long>> input, Collector<String> out) throws Exception {

                        String key = tuple.toString();
                        ArrayList<Long> arrayList = new ArrayList<>();
                        Iterator<Tuple2<String,Long>> inputIterator = input.iterator();
                        while (inputIterator.hasNext()) {
                            Tuple2<String, Long> next = (Tuple2<String, Long>) inputIterator.next();
                            arrayList.add(next.f1);
                        }
                        Collections.sort(arrayList);
                        String result = key + ","
                                + arrayList.size() + ","
                                + sdf.format(arrayList.get(0)) + ","
                                + sdf.format(arrayList.get(arrayList.size() - 1)) + ','
                                + sdf.format(timeWindow.getStart()) + ","
                                + sdf.format(timeWindow.getEnd());
                        out.collect(result);
                    }
                });

        apply.print();
        // 收集被丢掉的数据
        DataStream<Tuple2<String, Long>> sideOutput = ((SingleOutputStreamOperator<String>) apply).getSideOutput(outputTag);
        sideOutput.print();
        // 执行
        environment.execute("StreamingWindowWatermark");

        /**
         * 测试用例
         *
         * 001,1538359872000
         * 002,1538359883000
         * 003,1538359884000
         * 004,1538359884000
         *
         * 005,1538359886000
         * 006,1538359887000
         *
         * 007,1538359888000
         *
         * 007,1538359883000
         *
         * 008,1538359893000
         * 008,1538359894000
         * 008,1538359899000
         *
         * 008,1538359839000
         * 008,1538359969000
         * 008,1538359979000
         * 009,1538359999000
         *
         */
    }


}

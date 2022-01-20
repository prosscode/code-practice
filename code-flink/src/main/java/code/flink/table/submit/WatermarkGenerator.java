package code.flink.table.submit;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;

import java.time.Duration;

/**
 * @date 2022/1/20
 * @created by shuang.peng
 * @description SqlParser
 */
public class WatermarkGenerator {
    public static AssignerWithPunctuatedWatermarks<Row> PunctuateFromColumn(int columnIndex, long outOfOrderSec) {
        return new AssignerWithPunctuatedWatermarks<Row>() {
            @Override
            public Watermark checkAndGetNextWatermark(Row lastElement, long extractedTimestamp) {
                return new Watermark(extractedTimestamp-outOfOrderSec*1000);
            }

            @Override
            public long extractTimestamp(Row element, long previousElementTimestamp) {
                return (Long) (element.getField(columnIndex)) * 1000;
            }
        };
    }

    public static AssignerWithPeriodicWatermarks<Row> BoundedFromColumn(int columnIndex, long outOfOrderSec) {
        return new BoundedOutOfOrdernessTimestampExtractor<Row>(Time.seconds(outOfOrderSec)) {
            @Override
            public long extractTimestamp(Row element) {
                return (Long) (element.getField(columnIndex)) * 1000;
            }
        };
    }

    public static WatermarkStrategy<Row> PunctuateAt(int columnIndex, long outOfOrderSec) {
        return new WatermarkStrategy<Row>() {
            @Override
            public org.apache.flink.api.common.eventtime.WatermarkGenerator<Row> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
                return new org.apache.flink.api.common.eventtime.WatermarkGenerator<Row>() {
                    @Override
                    public void onEvent(Row event, long eventTimestamp, WatermarkOutput output) {
                        output.emitWatermark(new org.apache.flink.api.common.eventtime.Watermark(eventTimestamp));
                    }
                    @Override
                    public void onPeriodicEmit(WatermarkOutput output) {
                    }
                };
            }
        }.withIdleness(Duration.ofSeconds(outOfOrderSec*3))
                .withTimestampAssigner(new TimestampAssignerSupplier<Row>() {
                    @Override
                    public TimestampAssigner<Row> createTimestampAssigner(Context context) {
                        return new TimestampAssigner<Row>() {
                            @Override
                            public long extractTimestamp(Row element, long recordTimestamp) {
                                return ((Long)element.getField(columnIndex)-outOfOrderSec)*1000;
                            }
                        };
                    }
                });
    }
}

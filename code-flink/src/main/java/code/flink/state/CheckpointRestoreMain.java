package code.flink.state;

import code.flink.entity.Order;
import code.flink.entity.OrderGenerator;
import code.flink.util.CheckpointUtil;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Objects;

/**
 * @Classname CheckpointRestoreMain
 * @Date 2020/12/15
 * @Description 从savepoint恢复数据
 * @Created by shuang.peng
 */
public class CheckpointRestoreMain {

    public static void main(String[] args) throws Exception {
        // 初始化环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        CheckpointUtil.setFsStateBackend(env);
        env.setParallelism(1);
        EnvironmentSettings mySetting = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, mySetting);
        // add source
        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
                .filter(Objects::nonNull);
        // 注册表
        tableEnv.createTemporaryView("order_table", orderStream,
                "ts, orderId, userId, goodsId, price, cityId");

        Table query = tableEnv.sqlQuery("select cityId, count(distinct userId) from order_table group by cityId");

        DataStream<Tuple2<Boolean, Row>> uvStream = tableEnv.toRetractStream(query, Row.class);

        uvStream.filter((FilterFunction<Tuple2<Boolean, Row>>) value -> value.f0)
                .map((MapFunction<Tuple2<Boolean, Row>, Row>) value -> value.f1)
                .print();

        // env.execute(CheckpointRestoreMain.class.getSimpleName());
        String externalCheckpoint = "file:///Users/shuang.peng/work/logs/flink-checkpoint/a99e8066174489595ee0bba8e2f0dfc9/chk-49";
        CheckpointRestoreUtils.run(env.getStreamGraph(), externalCheckpoint);

    }
}

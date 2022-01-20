package code.flink.table.submit;

import code.flink.table.submit.conf.DeduplicatorConfig;
import code.flink.table.submit.conf.SqlParser;
import code.flink.table.udf.GeoHash;
import code.flink.table.udf.JsonValueExtractor;
import code.flink.table.udf.PB2JsonConverter;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.roaringbitmap.RoaringBitmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @date 2022/1/18
 * @created by shuang.peng
 * @description StreamIntervalCompressor
 */
public class StreamIntervalCompressor {
    private static final Logger logger = LoggerFactory.getLogger(StreamIntervalCompressor.class);

    public static void main(String[] args) throws Exception {
        Optional<DeduplicatorConfig> configOpt = DeduplicatorConfig.fromArgs(args);
        if (!configOpt.isPresent()) {
            logger.error("error in construct config for IntervalCompressor");
            System.exit(-2);
        }
        DeduplicatorConfig config = configOpt.get();

        // env preparation
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().registerKryoType(RoaringBitmap.class);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        Configuration conf = tableEnv.getConfig().getConfiguration();
        conf.setBoolean("table.exec.emit.early-fire.enabled", true);
        conf.setString("table.exec.emit.early-fire.delay", "1s");

        Map<String,String> jobConf = env.getConfig().getGlobalJobParameters().toMap();
        if(jobConf.containsKey("yarn.application.name")){
            conf.setString("pipeline.name",jobConf.get("yarn.application.name"));
        } else {
            conf.setString("pipeline.name","kafka_to_hbase_stream_"+config.GetSinkTable());
        }
        //table.exec.emit.unchanged.enabled = true
        tableEnv.createTemporarySystemFunction("get_json_object", new JsonValueExtractor());
        tableEnv.createTemporarySystemFunction("geo_hash", new GeoHash());
        tableEnv.createTemporarySystemFunction("pb2json", new PB2JsonConverter());
        // graph build
        List<String> sql = Files.readAllLines(Paths.get(config.GetSqlFilePath()));
        List<SqlParser.SqlCommandCall> commands = SqlParser.Parse(sql);
        commands.add(0, new SqlParser.SqlCommandCall(SqlParser.SqlCommand.CREATE_TABLE, new String[]{config.GetSinkDdl()}));
        commands.forEach(x -> logger.info(String.format("parsed sql:%s", x.toString())));
        Optional<DataStream<Row>> unionDsOpt = commands.stream()
                .filter(cmd->cmd.command != SqlParser.SqlCommand.INSERT_INTO).flatMap(
                        cmd -> cmd.Call(tableEnv, config).stream()).reduce((x, y) -> x.union(y));
        if(unionDsOpt.isPresent()) {
            tableEnv.createTemporaryView("union_ds", unionDsOpt.get() ,
                    $("rowkey"), $("qf"), $("value"));
            tableEnv.executeSql(String.format("insert into %s select * from union_ds", config.GetSinkTable()));
        } else {
            logger.info("no specified view is created, all sql are standard flink sql");
            List<SqlParser.SqlCommandCall> insertCommands = commands.stream()
                    .filter(cmd->cmd.command == SqlParser.SqlCommand.INSERT_INTO)
                    .collect(Collectors.toList());

            if(!insertCommands.isEmpty()){
                logger.info("following insert sql are executed in a statement set");
                insertCommands.forEach(cmd ->logger.info(cmd.operands[0]));
                StatementSet statementSet = tableEnv.createStatementSet();
                insertCommands.forEach(cmd -> statementSet.addInsertSql(cmd.operands[0]));
                statementSet.execute();
            } else {
                logger.error("no insert is specified!!!");
            }

        }

    }

}

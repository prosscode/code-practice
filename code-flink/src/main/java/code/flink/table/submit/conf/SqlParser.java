package code.flink.table.submit.conf;

import code.flink.table.submit.WatermarkGenerator;
import code.flink.table.submit.operands.AggregatorStrategies;
import code.flink.table.submit.operands.ColumnIndexConstants;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeoutTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.SqlParserException;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @date 2022/1/18
 * @created by shuang.peng
 * @description SqlParser
 */
public class SqlParser {

    private static final Logger logger = LoggerFactory.getLogger(SqlParser.class);

    public static List<SqlCommandCall> Parse(List<String> lines) {
        List<SqlCommandCall> calls = new ArrayList<>();
        StringBuilder stmt = new StringBuilder();
        for (String line : lines) {
            if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                // skip empty line and comment line
                continue;
            }
            stmt.append("\n").append(line);
            if (line.trim().endsWith(";")) {
                Optional<SqlCommandCall> optionalCall = Parse(stmt.toString());
                if (optionalCall.isPresent()) {
                    calls.add(optionalCall.get());
                } else {
                    throw new RuntimeException("Unsupported command '" + stmt.toString() + "'");
                }
                // clear string builder
                stmt.setLength(0);
            }
        }
        return calls;
    }

    public static Optional<SqlCommandCall> Parse(String stmt) {
        // normalize
        stmt = stmt.trim();
        // remove ';' at the end
        if (stmt.endsWith(";")) {
            stmt = stmt.substring(0, stmt.length() - 1).trim();
        }

        // parse
        for (SqlCommand cmd : SqlCommand.values()) {
            final Matcher matcher = cmd.pattern.matcher(stmt);
            if (matcher.matches()) {
                final String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < groups.length; i++) {
                    groups[i] = matcher.group(i + 1);
                }
                return cmd.operandConverter.apply(groups)
                        .map(operands -> new SqlCommandCall(cmd, operands));
            }
        }
        return Optional.empty();
    }

    // --------------------------------------------------------------------------------------------

    /*private static final Function<String[], Optional<String[]>> NO_OPERANDS =
            (operands) -> Optional.of(new String[0]);*/

    private static final Function<String[], Optional<String[]>> SINGLE_OPERAND =
            (operands) -> Optional.of(new String[]{operands[0]});

    private static final int DEFAULT_PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;

    /**
     * Supported SQL commands.
     */
    public enum SqlCommand {
        INSERT_INTO(
                "(INSERT\\s+INTO.*)",
                SINGLE_OPERAND),

        CREATE_TABLE(
                "(CREATE\\s+TABLE.*)",
                SINGLE_OPERAND),
        /* SELECT_TABLE(
                 "(SELECT\\s+.*)",
                 SINGLE_OPERAND),*/
        CREATE_VIEW(
                "CREATE\\s+VIEW\\s+(\\S+)\\s+AS\\s+(.*)",
                operands -> Optional.of(new String[]{operands[0], operands[1]})),

        SET(
                //    "SET(\\s+(\\S+)\\s*=\\s*(\\S+))?", // whitespace is only ignored on the left side of '='
                "SET(\\s+(\\S+)\\s*=\\s*(.*))?", // whitespace is only ignored on the left side of '='

                (operands) -> {
                    if (operands.length < 3) {
                        return Optional.empty();
                    } else if (operands[0] == null) {
                        return Optional.of(new String[0]);
                    }
                    return Optional.of(new String[]{operands[1], operands[2]});
                });

        public final Pattern pattern;
        public final Function<String[], Optional<String[]>> operandConverter;

        SqlCommand(String matchingRegex, Function<String[], Optional<String[]>> operandConverter) {
            this.pattern = Pattern.compile(matchingRegex, DEFAULT_PATTERN_FLAGS);
            this.operandConverter = operandConverter;
        }

        @Override
        public String toString() {
            return super.toString().replace('_', ' ');
        }

    }

    /**
     * Call of SQL command with operands and command type.
     */
    public static class SqlCommandCall {
        public final SqlCommand command;
        public final String[] operands;

        public SqlCommandCall(SqlCommand command, String[] operands) {
            this.command = command;
            this.operands = operands;
        }

        public List<DataStream<Row>> Call(StreamTableEnvironment tableEnv, DeduplicatorConfig config) {
            logger.info("execute sql:" + Arrays.toString(operands));
            switch (command) {
                case SET:
                    String key = operands[0];
                    String value = operands[1];
                    tableEnv.getConfig().getConfiguration().setString(key.trim(), value.trim());
                    break;
                case CREATE_TABLE:
                case INSERT_INTO:
                    String ddl = operands[0];
                    try {
                        logger.info("create or insert table sql:" + ddl);
                        tableEnv.executeSql(ddl);
                    } catch (SqlParserException e) {
                        throw new RuntimeException("SQL parse failed:\n" + ddl + "\n", e);
                    }
                    break;
                case CREATE_VIEW:
                    String viewName = operands[0];
                    String selectStat = operands[1];
                    logger.info(String.format("viewName:%s, select statement:%s", viewName, selectStat));
                    return createView(tableEnv, viewName, selectStat, config);
                default:
                    throw new RuntimeException("Unsupported command: " + command);
            }
            return Collections.EMPTY_LIST; // finished tasks will not trigger checkpoint
            /*tableEnv.toAppendStream(tableEnv.fromValues(
                    DataTypes.ROW(
                        DataTypes.FIELD("rk", DataTypes.STRING()),
                        DataTypes.FIELD("qf", DataTypes.STRING()),
                        DataTypes.FIELD("val", DataTypes.STRING())
                    )),
                    Types.ROW(Types.STRING, Types.STRING, Types.STRING));*/
        }

        private List<DataStream<Row>> createView(StreamTableEnvironment tableEnv, String viewName, String selectStat, DeduplicatorConfig config) {
            MetricCategory category = categoryFromName(viewName);
            if (category == MetricCategory.Unknown) { // non-metric view
                tableEnv.executeSql(String.format("create view %s as %s", viewName, selectStat));
                return Collections.EMPTY_LIST;
            }


            Table idValueTsTable = tableEnv.sqlQuery(selectStat);
            TableSchema schema = idValueTsTable.getSchema();
            if (!verifySchema(schema)) throw new IllegalArgumentException("illegal schema:" + schema.toString());
            DataStream<Row> idValueTsDS = tableEnv.toAppendStream(idValueTsTable, schema.toRowType());
            if (category == MetricCategory.Val) {
                DataStream<Row> result = idValueTsDS.map(r -> {
                    String id = r.getField(0).toString();
                    String value = r.getField(1).toString();
                    Long ts = (Long) r.getField(2);
                    return Row.of(String.format("%s_%d", id, ts), viewName, value);
                }).returns(Types.ROW(Types.STRING, Types.STRING, Types.STRING));
                return Collections.singletonList(result);
            }

            // 1) specify watermark generator
            SingleOutputStreamOperator<Row> eventTimeDS = idValueTsDS
                    .filter(r -> {
                        Long ts = Long.parseLong(r.getField(2).toString());
                        Long systemTs = System.currentTimeMillis() / 1000;
                        if (ts - config.GetFutureLogSec() > systemTs)
                            logger.error(String.format("ts:%d is %d seconds greater than systemMs:%d  with row:%s", ts, ts - systemTs, systemTs, r.toString()));
                        return config.GetFutureEventSec() < 0 || ts - config.GetFutureEventSec() < systemTs;
                    })
                    .assignTimestampsAndWatermarks(
                            //WatermarkGenerator.PunctuateFromColumn(ColumnIndexConstants.TsIndex, config.GetOutOfOrderSec())
                            WatermarkGenerator.BoundedFromColumn(ColumnIndexConstants.TsIndex, config.GetOutOfOrderSec())
                            //WatermarkGenerator.PunctuateAt(ColumnIndexConstants.TsIndex, config.GetOutOfOrderSec())
                    );

            final OutputTag<Row> lateOutputTag = new OutputTag<Row>(String.format("late-not-dropped-data-%s", viewName)) {
            };

            // 2) process rows in tumbling window
            WindowedStream<Row, String, TimeWindow> windowedStream = eventTimeDS.keyBy(row -> (String) row.getField(0))
                    .window(TumblingEventTimeWindows.of(Time.seconds(config.GetIntervalSec())))
                    .trigger(ProcessingTimeoutTrigger.of(EventTimeTrigger.create(),
                            Duration.ofSeconds(config.GetProcessWindowTimeoutSec()), true, true)) //clear window state
                    .allowedLateness(Time.seconds(config.GetLatenessSec()))
                    .sideOutputLateData(lateOutputTag);
            SingleOutputStreamOperator<Row> windowDS = AggregatorStrategies.AggByCategory(windowedStream,
                    category, config.GetIntervalSec(), viewName);
            DataStream<Row> result = Objects.requireNonNull(windowDS).returns(Types.ROW(Types.STRING, Types.STRING, Types.STRING));
            logger.info("explain sql:" + tableEnv.fromDataStream(result, $("rowkey"), $("qf"), $("value")).explain());
            // 3) process side output rows
            DataStream<Row> sideOutput = AggregatorStrategies.AggByCategory(
                    windowDS.getSideOutput(lateOutputTag), category, config.GetIntervalSec(), viewName);
            return Collections.singletonList(result.union(sideOutput));
        }

        private List<DataStream<Row>> createView2(StreamTableEnvironment tableEnv, String viewName, String selectStat, DeduplicatorConfig config) {
            MetricCategory category = categoryFromName(viewName);
            if (category == MetricCategory.Unknown) { // non-metric view
                tableEnv.executeSql(String.format("create view %s as %s", viewName, selectStat));
                return Collections.EMPTY_LIST;
            }


            Table idValueTsTable = tableEnv.sqlQuery(selectStat);
            TableSchema schema = idValueTsTable.getSchema();
            if (!verifySchema2(schema)) throw new IllegalArgumentException("illegal schema:" + schema.toString());
            DataStream<Row> idValueTsDS = tableEnv.toAppendStream(idValueTsTable, schema.toRowType());
            if (category == MetricCategory.Val) {
                DataStream<Row> result = idValueTsDS.map(r -> {
                    String id = r.getField(0).toString();
                    String value = r.getField(1).toString();
                    Long ts = (Long) r.getField(2);
                    Row kafkaOffset = (Row)r.getField(3);
                    return Row.of(String.format("%s_%d", id, ts), viewName, value, kafkaOffset.toString());
                }).returns(Types.ROW(Types.STRING, Types.STRING, Types.STRING,Types.STRING));
                return Collections.singletonList(result);
            }

            // 1) specify watermark generator
            SingleOutputStreamOperator<Row> eventTimeDS = idValueTsDS
                    .filter(r -> {
                        Long ts = Long.parseLong(r.getField(2).toString());
                        Long systemTs = System.currentTimeMillis() / 1000;
                        if (ts - config.GetFutureLogSec() > systemTs)
                            logger.error(String.format("ts:%d is %d seconds greater than systemMs:%d  with row:%s", ts, ts - systemTs, systemTs, r.toString()));
                        return config.GetFutureEventSec() < 0 || ts - config.GetFutureEventSec() < systemTs;
                    })
                    .assignTimestampsAndWatermarks(
                            //WatermarkGenerator.PunctuateFromColumn(ColumnIndexConstants.TsIndex, config.GetOutOfOrderSec())
                            WatermarkGenerator.BoundedFromColumn(ColumnIndexConstants.TsIndex, config.GetOutOfOrderSec())
                            //WatermarkGenerator.PunctuateAt(ColumnIndexConstants.TsIndex, config.GetOutOfOrderSec())
                    );

            final OutputTag<Row> lateOutputTag = new OutputTag<Row>(String.format("late-not-dropped-data-%s", viewName)) {
            };

            // 2) process rows in tumbling window
            WindowedStream<Row, String, TimeWindow> windowedStream = eventTimeDS.keyBy(row -> (String) row.getField(0))
                    .window(TumblingEventTimeWindows.of(Time.seconds(config.GetIntervalSec())))
                    .trigger(ProcessingTimeoutTrigger.of(EventTimeTrigger.create(),
                            Duration.ofSeconds(config.GetProcessWindowTimeoutSec()), true, true)) //clear window state
                    .allowedLateness(Time.seconds(config.GetLatenessSec()))
                    .sideOutputLateData(lateOutputTag);
            SingleOutputStreamOperator<Row> windowDS = AggregatorStrategies.AggByCategory2(windowedStream,
                    category, config.GetIntervalSec(), viewName);
            DataStream<Row> result = Objects.requireNonNull(windowDS).returns(Types.ROW(Types.STRING, Types.STRING, Types.STRING,Types.STRING));
            logger.info("explain sql:" + tableEnv.fromDataStream(result, $("rowkey"), $("qf"), $("value"),$("kafka_offset")).explain());
            // 3) process side output rows
            // TODO AggByCategory2 with kafka offset
            DataStream<Row> sideOutput = AggregatorStrategies.AggByCategory(
                    windowDS.getSideOutput(lateOutputTag), category, config.GetIntervalSec(), viewName);
            return Collections.singletonList(result.union(sideOutput));
        }

        private static MetricCategory categoryFromName(String viewName) {
            if (viewName.endsWith("_cnt")) return MetricCategory.Count;
            if (viewName.endsWith("_unq")) return MetricCategory.Distinct;
            if (viewName.endsWith("_lst")) return MetricCategory.Last;
            if (viewName.endsWith("_min")) return MetricCategory.Min;
            if (viewName.endsWith("_max")) return MetricCategory.Max;
            if (viewName.endsWith("_val")) return MetricCategory.Val;

            return MetricCategory.Unknown;
        }

        private static boolean verifySchema(TableSchema schema) {
            if (schema.getFieldNames().length != 3) {
                logger.error("extracted Sql must has three columns:(id,value,ts)");
                return false;
            }
            DataType[] dataTypes = schema.getFieldDataTypes();
            DataType[] expectDataTypes = new DataType[]{DataTypes.STRING(), DataTypes.STRING(), DataTypes.BIGINT()};
            Object[] nullableDataTypes = Arrays.stream(dataTypes).map(DataType::nullable).toArray();
            if (!Arrays.deepEquals(nullableDataTypes, expectDataTypes)) {
                logger.error(String.format("extracted Sql must has schema(STRING,STRING,BIGINT), actual data types:%s",
                        Arrays.toString(nullableDataTypes)));
                return false;
            }
            return true;
        }

        private static boolean verifySchema2(TableSchema schema) {
            if (schema.getFieldNames().length != 4) {
                logger.error("extracted Sql must has three columns:(id,value,ts,{topic, part, offset})");
                return false;
            }
            DataType[] dataTypes = schema.getFieldDataTypes();
            DataType[] expectDataTypes = new DataType[]{DataTypes.STRING(), DataTypes.STRING(), DataTypes.BIGINT(),
                    DataTypes.ROW(
                            DataTypes.FIELD("topic",DataTypes.STRING()),
                            DataTypes.FIELD("part",DataTypes.INT()),
                            DataTypes.FIELD("offset",DataTypes.BIGINT())
                    )};
            Object[] nullableDataTypes = Arrays.stream(dataTypes).map(DataType::nullable).toArray();
            if (!Arrays.deepEquals(nullableDataTypes, expectDataTypes)) {
                logger.error(String.format("extracted Sql must has schema(STRING,STRING,BIGINT,ROW(STRING,INT,BIGINT)), actual data types:%s",
                        Arrays.toString(nullableDataTypes)));
                return false;
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SqlCommandCall that = (SqlCommandCall) o;
            return command == that.command && Arrays.equals(operands, that.operands);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(command);
            result = 31 * result + Arrays.hashCode(operands);
            return result;
        }

        @Override
        public String toString() {
            return command + "(" + Arrays.toString(operands) + ")";
        }
    }

}

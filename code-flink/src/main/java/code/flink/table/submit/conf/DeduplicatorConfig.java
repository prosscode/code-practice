package code.flink.table.submit.conf;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Optional;

/**
 * @date 2022/1/19
 * @created by shuang.peng
 * @description DeduplicatorConfig
 */
public class DeduplicatorConfig implements Serializable {

    private static final long serialVersionUID = 1l;
    private static final Logger logger = LoggerFactory.getLogger(DeduplicatorConfig.class);

    private String sqlFilePath;
    private String sinkTable;
    private String zkQuorum;
    private long outOfOrderSec;
    private long latenessSec;
    private long intervalSec;
    private long futureEventSec;
    private long futureLogSec;
    private long processWindowTimeoutSec;

    public static Optional<DeduplicatorConfig> fromArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        final Options options = new Options();
        options.addOption("sql", true, "sql file path");
        options.addOption("sinkTable", true, "hbase table to sink");
        options.addOption("zkQuorum", true, "zookeeper quorum for hbase cluster");
        options.addOption("outOfOrderSec", true, "outOfOrder sec in BoundedOutOfOrdernessTimestampExtractor");
        options.addOption("intervalSec", true, "interval width in sec to compress");
        options.addOption("latenessSec", true, "allowed lateness in sec");
        options.addOption("futureEventSec", true, "allowed future event  in sec");
        options.addOption("futureLogSec", true, "log records whose ts is far after current watermark");
        options.addOption("processTimeoutSec", true, "process event timeout in sec in triggering window's emit");

        options.addOption("h", "help", false, "help message");


        try {
            CommandLine commandLine = parser.parse(options, args);
            if (args.length == 0 || commandLine.hasOption('h')) {
                logger.error("Usage:%s\n", options.toString());
                return Optional.empty();
            }
            DeduplicatorConfig config = new DeduplicatorConfig();
            config.sqlFilePath = commandLine.getOptionValue("sql");
            config.sinkTable = commandLine.getOptionValue("sinkTable");
            config.zkQuorum = commandLine.getOptionValue("zkQuorum",
                    "zookeeper-server-01:2181,zookeeper-server-02:2181,zookeeper-server-03:2181");
            config.outOfOrderSec = Integer.parseInt(commandLine.getOptionValue("outOfOrderSec", "60"));
            config.latenessSec = Integer.parseInt(commandLine.getOptionValue("latenessSec", "300"));
            config.intervalSec = Integer.parseInt(commandLine.getOptionValue("intervalSec"));
            config.futureEventSec = Integer.parseInt(commandLine.getOptionValue("futureEventSec","-1"));
            config.futureLogSec = Integer.parseInt(commandLine.getOptionValue("futureLogSec","60"));
            config.processWindowTimeoutSec = Integer.parseInt(commandLine.getOptionValue("processWindowTimeoutSec","10"));

            return Optional.of(config);
        } catch (Exception ex) {
            logger.error("error in parsing deduplicator config from args:" + ex.getMessage());
            return Optional.empty();
        }
    }

    public String GetSqlFilePath() {
        return sqlFilePath;
    }

    //for ns:table_name return table_name only
    public String GetSinkTable() {
        String[] attrs = sinkTable.split(":");
        return  attrs[attrs.length-1];
    }

    public long GetLatenessSec() {
        return latenessSec;
    }

    public long GetOutOfOrderSec() {
        return outOfOrderSec;
    }

    public long GetIntervalSec() {
        return intervalSec;
    }

    public long GetFutureEventSec() {
        return futureEventSec;
    }

    public long GetFutureLogSec() {
        return futureLogSec;
    }

    public long GetProcessWindowTimeoutSec() {
        return processWindowTimeoutSec;
    }

    public String GetSinkDdl() {
        return String.format("create table %s (" +
                " rowkey STRING," +
                " qf STRING," +
                " val STRING ) with (" +
                " 'connector.type' = 'hbase-ts'," +
                " 'connector.version' = 'streaming'," +
                " 'connector.property-version' = '1'," +
                " 'connector.table-name' = '%s'," +
                " 'connector.zookeeper.quorum' = '%s'," +
                " 'connector.write.buffer-flush.max-size' ='1mb'," +
                " 'connector.write.buffer-flush.max-rows' ='200'," +
                " 'connector.write.buffer-flush.interval' ='500'" +
                ")", GetSinkTable(), sinkTable, zkQuorum);

    }

}

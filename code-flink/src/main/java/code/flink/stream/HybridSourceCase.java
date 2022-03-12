package code.flink.stream;

import org.apache.flink.connector.base.source.hybrid.HybridSource;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineFormat;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.Path;

import java.io.File;

/**
 * @Date 2022/3/12
 * @Author by shuang.peng
 * @Description HybridSource
 */
public class HybridSourceCase {

    public static void main(String[] args) {

        // derive from file input paths
        long switchTimestamp = 1;
        FileSource<String> fileSource = FileSource.forRecordStreamFormat(
                new TextLineFormat(),
                Path.fromLocalFile(new File("path"))
        ).build();

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder().
                setStartingOffsets(OffsetsInitializer.timestamp(switchTimestamp + 1))
                .build();

        HybridSource<String> hybridSource = HybridSource
                .builder(fileSource)
                .addSource(kafkaSource)
                .build();

        // customFileSource
    }

}

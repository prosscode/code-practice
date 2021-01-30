package code.kafka.consumer.asyncProcessMsg;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

/**
 * @describe: Kafka Consumer多线程消费
 * @author: 彭爽 pross.peng
 * @date: 2020/05/06
 */
public class ConsumerWorker<K,V> implements Runnable {

    private ConsumerRecords<K,V> records;
    private Map<TopicPartition, OffsetAndMetadata> offsets;

    public ConsumerWorker(ConsumerRecords<K, V> records, Map<TopicPartition, OffsetAndMetadata> offsets) {
        this.records = records;
        this.offsets = offsets;
    }

    @Override
    public void run() {

    }


}

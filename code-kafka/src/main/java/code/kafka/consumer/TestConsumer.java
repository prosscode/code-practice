package code.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description 消费者
 * @Date 2021/1/30
 * @Created by shuang.peng
 */
public class TestConsumer {
    public static final AtomicBoolean isRunning=new AtomicBoolean(true);
    public static final String brokerList="kafka01:9092,kafka02:9092";
    public static final String topic="dataTopic";
    public static final String groupId="dataTopic-groupid";

    public static void main(String[] args) {
        //配置消费者客户端参数
        Properties properties= initConfig();
        //创建相应的消费者实例
        KafkaConsumer<String,String> consumer=new KafkaConsumer<>(properties);
        //订阅主题
        consumer.subscribe(Arrays.asList(topic));

        try {
            //拉取消息并消费
            while(isRunning.get()){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                // do something to process record.
                process(records);
                // 使用异步提交规避阻塞
                consumer.commitAsync();
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }finally {
            try {
                // 使用同步阻塞式提交
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private static void process(ConsumerRecords<String, String> records) {

    }
    /**
     * 配置消费者客户端参数
     * */
    private static Properties initConfig() {
        Properties properties=new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Deserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Deserializer.class.getName());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        //指定连接Kafka集群所需的broker地址清单,中间用逗号隔开,默认值为""
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        //消费者所属的消费组的名称,默认值为""
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //指定消费者客户端Id,如果不设置,则自动生成consumer-1,consumer-2
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG,"consumer.client.id");

        return properties;
    }
}

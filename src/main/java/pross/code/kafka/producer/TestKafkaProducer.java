package pross.code.kafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @describe: 模拟产生测试数据
 * @author: 彭爽 pross.peng
 * @date: 2020/08/09
 */
public class TestKafkaProducer {

    public static void main(String[] args) throws InterruptedException {
        Properties prop = new Properties();
        // 执行broker地址
        prop.put("bootstrap.server","kafka01:9092,kafka02:9092");
        // 指定序列化方式
        prop.put("key.serializer", StringSerializer.class.getName());
        prop.put("value.serializer", StringSerializer.class.getName());
        // 执行topic名称
        String topic = "dataTopic";

        // 创建Produer连接
        KafkaProducer<String, String> producer = new KafkaProducer<>(prop);

        // 生产消息
        while(true){
            String message = "";

            producer.send(new ProducerRecord<>(topic, message), new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {

                }
            });
            Thread.sleep(1000);
            break;
        }
        // 关闭链接
        producer.close();
    }
}

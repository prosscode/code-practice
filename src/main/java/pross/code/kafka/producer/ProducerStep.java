package pross.code.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * @describe:
 * @author: 彭爽 pross.peng
 * @date: 2020/05/08
 */
public class ProducerStep {
    private static Logger logger = LoggerFactory.getLogger(ProducerStep.class);

    /**
     * producer开发五步走：
     * 1。构建参数对象
     * 2。创建producer实例
     * 3。send发送消息(callback)
     * 4。close关闭和释放资源
     */
    private Random random = new Random();
    private KafkaProducer producer;

    // create producer
    public void createProducer() {
        // 增加拦截器配置
        List<String> interceptors = new ArrayList<>();
        interceptors.add("org.code.kafka.interceptor.AvgLatencyProducerInterceptor");
        // producer config
        Properties props = new Properties();
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);
        // 创建kafkaProducer实例
        producer = new KafkaProducer<>(props);
    }

    // send message
    public void sendMessage(String topic,String message){
        //send message
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, random.nextInt(3), "", message);
        //send方法是异步的,添加消息到缓存区等待发送,并立即返回，这使生产者通过批量发送消息来提高效率
        producer.send(record, new Callback() {
            // 无论失败还是成功，必须有回调返回
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (null != exception) {
                    logger.error("kafka发送消息失败:" + exception.getMessage(),exception);
                    // 失败重试
                    sendMessage(topic,message);
                }
            }
        });
        producer.close();
    }
}

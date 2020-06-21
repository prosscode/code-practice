package pross.code.kafka.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @describe: kafka 拦截器，生产者拦截器
 *      需求：业务消息从被生产出来到最后被消费的平均总时长是多少，统计端到端的平均延时
 * @author: 彭爽 pross.peng
 * @date: 2020/05/07
 */
public class AvgLatencyProducerInterceptor  implements ProducerInterceptor<String,String> {

    // private Jedis jedis;
    // 省略Jedis初始化， 用map代替统计,需要保证线程安全（producerIntercepter和consumerInterceptor是不同的线程）
    static ConcurrentHashMap<String,Object> messageMap;
    static {
        messageMap.put("totalSentMessage",0);
        messageMap.put("totalLatency",0);
        messageMap.put("avgLatency",0);
    }

    /**
     * onsend方法，会在消息发送之前被调用，需要考虑消息发送失败的情况
     * @param producerRecord
     * @return
     */
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> producerRecord) {
        // jedis.incr("totalSentMessage);

        // 在发送消息前更新总的已发送消息数
        Long totalSentMessage = Long.parseLong(messageMap.get("totalSentMessage").toString());
        totalSentMessage += 1 ;
        messageMap.put("totalSentMessage",totalSentMessage);
        return producerRecord;
    }

    /**
     * onAcknowledgement方法，在消息被确定提交后被调用（无论消息提交失败还是成功）
     * @param recordMetadata
     * @param e
     */
    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}

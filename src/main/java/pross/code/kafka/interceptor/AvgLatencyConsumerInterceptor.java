package pross.code.kafka.interceptor;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

/**
 * @describe: 消费者拦截器
 * @author: 彭爽 pross.peng
 * @date: 2020/05/07
 */
public class AvgLatencyConsumerInterceptor implements ConsumerInterceptor<String,String> {
    // 省略Jedis初始化， 用map代替统计
    // private Jedis jedis;

    /**
     * onConsume放方法，会在消息被消费前被调用
     * @param consumerRecords
     * @return
     */
    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> consumerRecords) {
        long lantency = 0L;
        long currentTimeMillis = System.currentTimeMillis();
        for (ConsumerRecord record : consumerRecords) {
            lantency += currentTimeMillis - record.timestamp();
        }

        // jedis.incrBy("totalLatency", lantency);
        AvgLatencyProducerInterceptor.messageMap.put("totalLatency",lantency);

//        long totalLatency = Long.parseLong(jedis.get("totalLatency"));
//        long totalSentMsgs = Long.parseLong(jedis.get("totalSentMessage"));

        long totalLatency = Long.parseLong(AvgLatencyProducerInterceptor.messageMap.get("totalLatency").toString());
        long totalSentMsgs = Long.parseLong(AvgLatencyProducerInterceptor.messageMap.get("totalSentMessage").toString());

        // 平均延时
//        jedis.set("avgLatency", String.valueOf(totalLatency / totalSentMsgs));
        AvgLatencyProducerInterceptor.messageMap.put("avgLatency", String.valueOf(totalLatency / totalSentMsgs));
        return consumerRecords;
    }

    /**
     * onCommit方法，在程序提交位移后被调用
     * @param map
     */
    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {
        // 合理的端到端的延时统计，是在onCommit方法中实现计算
        // 但consumerRecords可以直接拿到messahe timestamp，比较方便
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}

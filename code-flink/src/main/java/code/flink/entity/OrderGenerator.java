package code.flink.entity;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Random;

/**
 * @Classname OrderGenerator
 * @Date 2020/12/15
 * @Description 数据生成器
 * @Created by shuang.peng
 */
public class OrderGenerator extends RichParallelSourceFunction<Order> {

    private static final Random random = new Random();
    private static long orderId = 0;

    private int userIdMax = 10_000_000;

    @Override
    public void run(SourceContext sourceContext) throws Exception {
        while (true) {
            int cityId = random.nextInt(10);
            if (cityId == 0) {
                sourceContext.collect(null);
            }

            String orderId = "orderId:" + OrderGenerator.orderId++;
            // 随机生成
            String userId = Integer.toString(random.nextInt(userIdMax));
            int goodsId = random.nextInt(10);
            int price = random.nextInt(10000);
            Order order = new Order(System.currentTimeMillis(),
                    orderId, userId, goodsId, price, cityId);
            sourceContext.collect(order);
            Thread.sleep(2000);
        }
    }

    @Override
    public void cancel() {

    }
}

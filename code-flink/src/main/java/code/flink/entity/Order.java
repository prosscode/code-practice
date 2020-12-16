package code.flink.entity;

import lombok.Data;

/**
 * @Classname Order
 * @Date 2020/12/15
 * @Description
 * @Created by shuang.peng
 */
@Data
public class Order {
    /** 订单发生的时间 */
    public long ts;
    /** 订单 id */
    public String orderId;
    /** 用户id */
    public String userId;
    /** 商品id */
    public int goodsId;
    /** 价格 */
    public long price;
    /** 城市 */
    public int cityId;

    public Order() {}

    public Order(long ts, String orderId, String userId, int goodsId, long price, int cityId) {
        this.ts = ts;
        this.orderId = orderId;
        this.userId = userId;
        this.goodsId = goodsId;
        this.price = price;
        this.cityId = cityId;
    }


}

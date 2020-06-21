package pross.code.pattern.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @describe:  多个连接的分享功能
 * @author: 彭爽 pross.peng
 * @date: 2020/04/27
 */
public class ShareFunction {

    private static List<DealContext> algs = new ArrayList<DealContext>();

    static {
        algs.add(new DealContext("sina",new DealSina()));
        algs.add(new DealContext("wechat",new DealWechat()));
    }


    public static void main(String[] args) {
        DealStrategy dealStrategy = null;
        for (DealContext deal : algs) {
            if(deal.options(deal.getType())){
                dealStrategy = deal.getDeal();
                break;
            }
            dealStrategy.dealMethod(deal.getType());
        }
    }
}

package code.java.base;


import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Date 2022/10/27
 * @Author by shuang.peng
 * @Description CommonRandom
 */
public class CommonRandom {
    // 最小值
    private int min;
    // 最大值
    private int max;
    //
    private Double chance;
    //
    private Double calculateRate;
    private transient Integer reality = 0;
    private transient Double realityRate;

    public static CommonRandom getInstance(int min, int max, Double rate) {
        CommonRandom commonRandom = new CommonRandom();
        commonRandom.setMin(min);
        commonRandom.setMax(max);
        commonRandom.setChance(rate);
        return commonRandom;
    }

    static List<CommonRandom> getCommonRandom() {
        List<CommonRandom> commonRandomList = Lists.newArrayList();
        commonRandomList.add(CommonRandom.getInstance(1, 100, 0.4));
        commonRandomList.add(CommonRandom.getInstance(100, 200, 0.5));
        commonRandomList.add(CommonRandom.getInstance(200, 300, 0.06));
        commonRandomList.add(CommonRandom.getInstance(300, 400, 0.03));
        commonRandomList.add(CommonRandom.getInstance(400, 500, 0.01));
        return commonRandomList;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Double getChance() {
        return chance;
    }

    public void setChance(Double chance) {
        this.chance = chance;
    }

    public Double getCalculateRate() {
        return calculateRate;
    }

    public void setCalculateRate(Double calculateRate) {
        this.calculateRate = calculateRate;
    }

}


class RandomUtil {
    /*** 保留小数位数*/
    private static final int POW = (int) Math.pow(10, 3);
    private static final double ERROR_RANDOM_RESULT = -1D;

    public static Double randomRedPackMoney() {
        List<CommonRandom> commonRandoms = CommonRandom.getCommonRandom();
        Double result = random(commonRandoms);
        if (result == ERROR_RANDOM_RESULT) {
            return 1d;
        }
        return result;
    }

    public static void main(String[] args) {
        randomRedPackMoney();
    }

    /**
     * 返回指定概率生成的随机数
     * 传入集合中的chance概率和应为1
     *
     * @param commonRandomList 配置生成随机数概率及区间
     * @return 随机数
     */
    public static Double random(List<CommonRandom> commonRandomList) {
        if (CollectionUtils.isEmpty(commonRandomList)) {
            return ERROR_RANDOM_RESULT;
        }
        double randomNumber = Math.random() * getLastRate(commonRandomList);

        for (CommonRandom item : commonRandomList) {
            if (randomNumber < item.getCalculateRate()) {
                return getRandomNumber(item.getMax(), item.getMin());
            }
        }
        return ERROR_RANDOM_RESULT;
    }

    private static double getLastRate(List<CommonRandom> commonRandomList) {
        return commonRandomList.get(commonRandomList.size() - 1).getCalculateRate();
    }

    private static Double getRandomNumber(int max, int min) {
        return Math.floor((Math.random() * (max - min) + min) * POW) / POW;
    }



}


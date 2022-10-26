package code.java.base;

import java.util.*;

/**
 * @Date 2022/10/26
 * @Author by shuang.peng
 * @Description CommonRandom
 */
public class Lottery {
    public static void main(String[] args) {

        List<Box> boxes = new ArrayList<>();
        // 序号==物品Id==物品名称==概率
        boxes.add(new Box(1, "P1", "物品1", 0.6d));
        boxes.add(new Box(2, "P2", "物品2", 0.2d));
        boxes.add(new Box(3, "P3", "物品3", 0.1d));
        boxes.add(new Box(4, "P4", "物品4", 0.002d));
        boxes.add(new Box(5, "P5", "物品5", 0d));
        boxes.add(new Box(6, "P6", "物品6", -0.1d));
        boxes.add(new Box(7, "P7", "物品7", 0.008d));

        List<Double> orignalRates = new ArrayList<>(boxes.size());
        for (Box Box : boxes) {
            double probability = Box.getProbability();
            if (probability < 0) {
                probability = 0;
            }
            orignalRates.add(probability);
        }

        // statistics
        Map<Integer, Integer> count = new HashMap<>();
        double num = 100;
        for (int i = 0; i < num; i++) {
            int orignalIndex = LotteryUtil.lottery(orignalRates);

            Integer value = count.get(orignalIndex);
            count.put(orignalIndex, value == null ? 1 : value + 1);
        }

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            System.out.println(boxes.get(entry.getKey()) + ", count=" + entry.getValue() + ", probability="
                    + entry.getValue() / num);
        }
    }

}

class Box {
    private int index;
    private String boxId;
    private String boxName;
    private double probability;

    public Box(int index, String boxId, String boxName, double probability) {
        this.index = index;
        this.boxId = boxId;
        this.boxName = boxName;
        this.probability = probability;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "Box [index=" + index + ", boxId=" + boxId + ", BoxName=" + boxName + ", probability="
                + probability + "]";
    }

}


class LotteryUtil {
    /**
     * 抽奖
     *
     * @param orignalRates 原始的概率列表，保证顺序和实际物品对应
     * @return 物品的索引
     */
    public static int lottery(List<Double> orignalRates) {
        if (orignalRates == null || orignalRates.isEmpty()) {
            return -1;
        }

        int size = orignalRates.size();

        // 计算总概率，这样可以保证不一定总概率是1
        double sumRate = 0d;
        for (double rate : orignalRates) {
            sumRate += rate;
        }

        // 计算每个物品在总概率的基础下的概率情况
        List<Double> sortOrignalRates = new ArrayList<>(size);
        Double tempSumRate = 0d;
        for (double rate : orignalRates) {
            tempSumRate += rate;
            sortOrignalRates.add(tempSumRate / sumRate);
        }

        // 根据区块值来获取抽取到的物品索引
        double nextDouble = Math.random();
        sortOrignalRates.add(nextDouble);
        Collections.sort(sortOrignalRates);

        return sortOrignalRates.indexOf(nextDouble);
    }

    public static int getJD(List<Double> orignalRates) {
        if (orignalRates == null || orignalRates.isEmpty()) {
            return -1;
        }

        int size = orignalRates.size();

        // 计算总概率，这样可以保证不一定总概率是1
        double sumRate = 0d;
        for (double rate : orignalRates) {
            sumRate += rate;
        }

        // 计算每个物品在总概率的基础下的概率情况
        List<Double> sortOrignalRates = new ArrayList<Double>(size);
        Double tempSumRate = 0d;
        for (double rate : orignalRates) {
            tempSumRate += rate;
            sortOrignalRates.add(tempSumRate / sumRate);
        }

        // 根据区块值来获取抽取到的物品索引
        double nextDouble = Math.random();
        sortOrignalRates.add(nextDouble);
        Collections.sort(sortOrignalRates);

        return sortOrignalRates.indexOf(nextDouble);
    }

}

package code.java.pattern.strategy;

/**
 * @describe: 策略模式 定义策略接口
 * @author: 彭爽 pross.peng
 * @date: 2020/04/27
 */
public interface DealStrategy {

    void dealMethod(String type);

}


class DealSina implements DealStrategy{

    @Override
    public void dealMethod(String type) {

    }
}


class DealWechat implements DealStrategy{

    @Override
    public void dealMethod(String type) {

    }
}


class DealContext{
    private String type;
    private DealStrategy deal;

    public DealContext(String type, DealStrategy deal) {
        this.type = type;
        this.deal = deal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DealStrategy getDeal() {
        return deal;
    }

    public void setDeal(DealStrategy deal) {
        this.deal = deal;
    }

    public boolean options(String type){
        return this.type.equals(type);
    }
}
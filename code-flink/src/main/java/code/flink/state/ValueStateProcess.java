package code.flink.state;

/**
 * @date 2022/3/11
 * @created by shuang.peng
 * @description ValueStateProcess
 */
public class ValueStateProcess {
    public String key;
    public int count;
    public long lastModified;

    public ValueStateProcess() {
        this.key = "";
        this.count = 0;
        this.lastModified = System.currentTimeMillis() / 1000;
    }

    @Override
    public String toString() {
        return "CountWithTimestamp{" +
                "key='" + key + '\'' +
                ", count=" + count +
                ", lastModified=" + lastModified +
                '}';
    }
}

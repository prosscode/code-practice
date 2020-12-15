package code.java.hadoop.writable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;

import java.io.IOException;

/**
 * @describe:
 * @author: 彭爽pross
 * @date: 2019/05/19
 */
public class TextWritableComparator extends WritableComparator {

    private static final Text.Comparator KEY_COMPARATOR = new Text.Comparator();

    // 调用父类的构造方法
    protected TextWritableComparator(){
        super(TextAndIntWritableComparable.class);
    }

    // 注册比较类
    static{
        WritableComparator.define(TextAndIntWritableComparable.class,new TextWritableComparator());
    }

    @Override
    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
        int result = 0;
        try{
            // 获取第一个值和第二个值的长度
            int firstLength = WritableUtils.decodeVIntSize((byte) (b1[s1] + readVInt(b1, s1)));
            int secondLength = WritableUtils.decodeVIntSize((byte) (b2[s2] + readVInt(b2, s2)));

            // 比较结果
            result = KEY_COMPARATOR.compare(b1, s1, firstLength, b2, s2, secondLength);
            if(result != 0){
                return result;
            }
            //对第二个值进行比较
            result = KEY_COMPARATOR.compare(b1,s1+firstLength,l1-firstLength,b2,s2+secondLength,l2-secondLength);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回结果
        return result;
    }

}

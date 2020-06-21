package pross.code.hadoop.writable;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @describe: 仿造hadoop基本包装类型。自定义一个writable，包含int和text两种类型。
 * @author: 彭爽pross
 * @date: 2019/05/19
 */
public class TextAndIntWritableComparable implements WritableComparable<TextAndIntWritableComparable> {

    private Text text;
    private IntWritable ints;

    public TextAndIntWritableComparable() {
        set(text,ints);
    }

    public void set(Text text,IntWritable ints){
        this.text = text;
        this.ints = ints;
    }

    public Text getText(){
        return text;
    }

    public IntWritable getInts(){
        return ints;
    }

    public int compareTo(TextAndIntWritableComparable o) {
        int compareTo = text.compareTo(o.getText());
        if (compareTo != 0){
            return compareTo;
        }
        return  ints.compareTo(o.getInts());
    }

    public void write(DataOutput output) throws IOException {
        text.write(output);
        ints.write(output);
    }

    public void readFields(DataInput input) throws IOException {
        text.readFields(input);
        ints.readFields(input);
    }

    // overwrite hashcode,tostring,equals5

}

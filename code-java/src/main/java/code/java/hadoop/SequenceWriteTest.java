package code.java.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.Random;

/**
 * @describe: hadoop jar xxx.jar SequenceWriteTest
 *      sequence file 对小文件记性存储和计算，将若干个小文件合并为一个大文件进行存储
 * @author: 彭爽pross
 * @date: 2019/05/19
 */
public class SequenceWriteTest {

    private static String meg = "hello world";

    public static void main(String[] args) throws IOException {

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path("sequenceFile");
        Random random = new Random();

        // 创建写入格式
        SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path, IntWritable.class, Text.class);
        for(int i =0;i<100;i++){
            // 写操作
            writer.append(new IntWritable(random.nextInt(100)),new Text(meg));
        }
        IOUtils.closeStream(writer);
    }
}

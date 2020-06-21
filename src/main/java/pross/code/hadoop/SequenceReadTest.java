package pross.code.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * @describe: sequence file 读操作
 * @author: 彭爽pross
 * @date: 2019/05/19
 */
public class SequenceReadTest {

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path("sequenceFile");

        SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
        IntWritable key = new IntWritable();
        Text value = new Text();
        while(reader.next(key,value)){
            System.out.println(key+"======"+value);
        }
        IOUtils.closeStream(reader);
    }
}

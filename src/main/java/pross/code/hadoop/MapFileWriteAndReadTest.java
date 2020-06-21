package pross.code.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * @describe: key out of order
 *      mapfile 继承 writeable comparable，对key进行了排序，提供查询功能
 * @author: 彭爽pross
 * @date: 2019/05/19
 */
public class MapFileWriteAndReadTest {

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path("mapfile.map");

        MapFile.Writer writer;
        writer = new MapFile.Writer(conf, fs, path.toString(), IntWritable.class, Text.class);
        for(int i =0;i<100;i++){
            // 写操作
            writer.append(new IntWritable(i),new Text("hello world"));
        }
        IOUtils.closeStream(writer);

        // 读操作
        MapFile.Reader reader;
        reader = new MapFile.Reader(fs, path.toString(), conf);
        IntWritable key = new IntWritable();
        Text value = new Text();
        while(reader.next(key,value)){
            System.out.println(key.toString()+"====="+value.toString());
        }
        IOUtils.closeStream(reader);
    }
}

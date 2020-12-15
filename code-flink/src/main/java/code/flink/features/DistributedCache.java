package code.flink.features;

import org.apache.commons.io.FileUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.configuration.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @describe: flink 分布式缓存
 *    缓存工作机制：通过executionEnvironment注册缓存文件，
 *               当程序执行时，flink自动将注册的缓存文件复制到所有的tm节点的本地文件系统
 *               可以通过为这个缓存文件起一个名字，方便从tm节点的本地文件系统访问
 * @author: 彭爽 pross.peng
 * @date: 2020/06/20
 */
public class DistributedCache {

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        /**
         * 三个参数：
         *  filepath：文件路径
         *  name：自定义名称
         *  executable：true/false
         */
        env.registerCachedFile("data/batch.txt","datacache",false);

        // init source
        DataSource<String> data = env.fromElements("a", "b", "c");

        MapOperator<String, Object> result = data.map(new RichMapFunction<String, Object>() {

            ArrayList dataList = new ArrayList<String>();

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                //
                File datacache = getRuntimeContext().getDistributedCache().getFile("datacache");
                List<String> lines = FileUtils.readLines(datacache);
                for (String line : lines) {
                    this.dataList.add(line);
                    System.out.println("line:" + line);
                }
            }

            @Override
            public Object map(String value) throws Exception {
                System.out.println("value:" + value);
                return value;
            }
        });

        result.print();

        env.execute("test");
    }
}

package pross.code.druid;

import com.google.common.collect.ImmutableMap;
import com.metamx.tranquility.config.DataSourceConfig;
import com.metamx.tranquility.config.PropertiesBasedConfig;
import com.metamx.tranquility.config.TranquilityConfig;
import com.metamx.tranquility.druid.DruidBeams;
import com.metamx.tranquility.tranquilizer.MessageDroppedException;
import com.metamx.tranquility.tranquilizer.Tranquilizer;
import com.twitter.util.FutureEventListener;
import org.joda.time.DateTime;
import scala.runtime.BoxedUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * @describe: 发送消息到druid.io，read json config
 * @author: 彭爽 pross.peng
 * @date: 2020/04/28
 */
public class TestDriudSendData {


    public static void main(String[] args) throws FileNotFoundException {
        // Read config from "example.json" on the classpath.
        File file = new File("./example.json");
//        FileInputStream inputStream = new FileInputStream(file);
        final InputStream configStream = new FileInputStream(file);
        final TranquilityConfig<PropertiesBasedConfig> config = TranquilityConfig.read(configStream);
        final DataSourceConfig<PropertiesBasedConfig> wikipediaConfig = config.getDataSource("wikipedia");
        final Tranquilizer<Map<String, Object>> sender = DruidBeams.fromConfig(wikipediaConfig)
                .buildTranquilizer(wikipediaConfig.tranquilizerBuilder());

        sender.start();

        for (int i = 0; i < 1000; i++) {
            // Build a sample event to send; make sure we use a current date
            final Map<String, Object> obj = ImmutableMap.<String, Object>of(
                    "timestamp", new DateTime().toString(),
                    "page", "foo",
                    "language","en",
                    "added", i
            );

            // Asynchronously send event to Druid:
            sender.send(obj).addEventListener(
                    new FutureEventListener<BoxedUnit>() {
                        @Override
                        public void onSuccess(BoxedUnit value) {
                            System.out.println("Sent message: " + obj);
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            if (e instanceof MessageDroppedException) {
                                System.out.println("Dropped message: %s" + obj);
                            } else {
                                System.out.println("Failed to send message: %s" + obj + "\n" + e);
                            }
                        }
                    }
            );
        }
        sender.flush();
        sender.stop();
    }
}

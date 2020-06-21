package pross.code

import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

/**
  * @describe:
  * @author: 彭爽pross
  * @date: 2019/07/29 
  */
object SocketWindowWordCount {

  def main(args: Array[String]): Unit = {
    // the port to connect to
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text: DataStreamSource[String] = env.socketTextStream("localhost",9999)

  }

}

package pross.code.rpc.thrift.impl;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import pross.code.rpc.HelloMethod;
import pross.code.rpc.RequestException;

/**
 * @describe: thrift client
 * @author: 彭爽pross
 * @date: 2019/06/20
 */
public class ThriftClient {

    private static void sendServerRequest() {
        try {
            // 创建客户端连接
            TSocket socket = new TSocket("localhost", 9090, 3000);
            // 设置TFramedTransport传输方式，二进制传输协议
            TTransport transport = new TFramedTransport(socket);
            TProtocol protocol = new TBinaryProtocol(transport);
            // open client
            socket.open();

            // 实例客户端业务
            HelloMethod.Client client = new HelloMethod.Client(protocol);
            //调用方法,返回结果
            String hello = client.sayHello("pross");
            int value = client.division(3, 1);
            System.out.println(hello);
            System.out.println("division value:"+value);

            socket.close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (RequestException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendServerRequest();
    }
}

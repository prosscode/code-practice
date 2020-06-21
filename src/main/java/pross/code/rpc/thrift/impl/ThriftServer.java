package pross.code.rpc.thrift.impl;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.*;
import pross.code.rpc.HelloMethod;

/**
 * @describe: thrift server
 * @author: 彭爽pross
 * @date: 2019/06/20
 */
public class ThriftServer{

    public static void initServer(){
        try {
            // 设置多线程服务模型
            TNonblockingServerSocket socket = new TNonblockingServerSocket(9090);
            // 处理关联业务TProcessor
            TProcessor processor = new HelloMethod.Processor((HelloMethod.Iface) new HelloThriftImpl());
            // 设置二进制传输协议，TFramedTransport传输方式和关联业务
            TNonblockingServer.Args arg = new TNonblockingServer.Args(socket);
            arg.protocolFactory(new TBinaryProtocol.Factory());
            arg.transportFactory(new TFramedTransport.Factory());
            arg.processorFactory(new TProcessorFactory(processor));

            // 开启服务
            TServer server = new TNonblockingServer (arg);
            System.out.println("start server port 9090 ...");
            server.serve();

        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initServer();
    }
}

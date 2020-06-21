package pross.code.rpc.thrift.hive;

import org.apache.hive.service.auth.PlainSaslHelper;
import org.apache.hive.service.rpc.thrift.TCLIService;
import org.apache.hive.service.rpc.thrift.TOpenSessionReq;
import org.apache.hive.service.rpc.thrift.TOpenSessionResp;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import javax.security.sasl.SaslException;

/**
 * @describe:
 * @author: 彭爽pross
 * @date: 2019/05/30
 */
public class QueryTool {

    public static TTransport getSocketInstance(String host, int port, String user, String pwd) throws TTransportException, SaslException {
        TSocket tSocket = new TSocket(host, port);
        tSocket.setTimeout(9999);
        TTransport transport = PlainSaslHelper.getPlainTransport(user, pwd, tSocket);
        return  transport;
    }

    public static TOpenSessionResp openSession(TCLIService.Client client) throws TException {
        TOpenSessionReq openSessionReq = new TOpenSessionReq();
        return client.OpenSession(openSessionReq);
    }
}
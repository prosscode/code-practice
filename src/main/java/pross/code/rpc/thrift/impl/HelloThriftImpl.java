package pross.code.rpc.thrift.impl;


import pross.code.rpc.HelloMethod;
import pross.code.rpc.RequestException;

/**
 * @describe:
 * @author: 彭爽pross
 * @date: 2019/06/22
 */
public class HelloThriftImpl implements HelloMethod.Iface {

    public int division(int param1, int param2) throws RequestException {
        int value = param1 / param2;
        return value;
    }

    public String sayHello(String username) {
        return "hello "+username;
    }
}

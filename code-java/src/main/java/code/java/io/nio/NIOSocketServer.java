package code.java.io.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @describe: 服务器端（NIOSocketServer）单个线程处理
 * @author:  shuang.peng
 * @date: 2020/07/06
 */
public class NIOSocketServer {

    private static Object xWait = new Object();

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            serverSocket = new ServerSocket(8888);
            //Java中非阻塞同步IO模式通过设置此方法，结合下面wait方法,实现连接状态的非阻塞（去accept阻塞）
            serverSocket.setSoTimeout(1000);
            while (true) {
                System.out.println("启动服务，等待消息...");
                Socket accept = null;
                try {
                    accept = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    /**
                     * 执行到这里，说明本次accept没有接收到任何数据报文
                     * 主线程在这里就可以做一些事情，记为X
                     */
                    synchronized (NIOSocketServer.xWait) {
                        System.out.println("没有从底层接收到任何数据，等待1秒，模拟事件X的处理时间");
                        NIOSocketServer.xWait.wait(1000);
                    }
                    // 继续等待
                    continue;
                }

                //下面我们收取信息
                in = accept.getInputStream();
                out = accept.getOutputStream();
                Integer sourcePort = accept.getPort();
                int maxLen = 2048;
                byte[] contextBytes = new byte[maxLen];
                //设置成非阻塞方式，这样read()信息的时候，又可以做一些其他事情
                int realLen;
                StringBuffer message = new StringBuffer();
                BIORead:while (true){
                    try {
                        accept.setSoTimeout(1000);
                        while((realLen = in.read(contextBytes, 0, maxLen)) != -1) {
                            message.append(new String(contextBytes, 0, realLen));
                            // 接收到over时，结束
                            if(message.indexOf("over") != -1) {
                                break BIORead;
                            }
                        }
                    }catch (SocketTimeoutException e){
                        /**
                         * 执行到这里，说明本次read没有接收到任何数据流
                         * 主线程在这里又可以做一些事情，记为Y
                         */
                        System.out.println("没有从客户端收到任何结束标志，等待1秒，模拟事件Y的处理时间");
                        continue ;
                    }
                }
                //下面打印信息
                System.out.println("服务器收到来自于端口：" + sourcePort + "的信息：" + message);

                //下面开始发送信息
                out.write("回发响应信息！".getBytes());
                //关闭
                out.close();
                in.close();
                accept.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

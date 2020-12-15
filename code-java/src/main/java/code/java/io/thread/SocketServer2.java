package code.java.io.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @describe: 服务器端（SocketServer2）多线程处理，实则上还是阻塞io
 * @author: 彭爽 pross.peng
 * @date: 2020/07/05
 */
public class SocketServer2 {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int incr = 0;
        try {
            serverSocket = new ServerSocket(8888);
            // 一直等待客户端连接
            while (true) {
                System.out.println("服务启动，等待消息...");
                Socket accept = serverSocket.accept();
                incr++;
                System.out.println("计数器："+incr);
                Thread.sleep(1000);
                /**
                 * 业务处理过程可以单独交给一个线程（这里可以使用线程池）,并且线程的创建是很耗资源的。
                 * 想说明的是accept(),read()，只能一个一个接受socket的情况,并且是被阻塞
                 */
                SocketServerThread serverThread = new SocketServerThread(accept);
                new Thread(serverThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


class SocketServerThread implements Runnable {

    private Socket accept;

    public SocketServerThread(Socket accept) {
        this.accept = accept;
    }

    @Override
    public void run() {
        //下面我们收取信息
        InputStream in = null;
        OutputStream out = null;
        try {
            in = accept.getInputStream();
            out = accept.getOutputStream();
            Integer sourcePort = accept.getPort();
            int maxLen = 2048;
            byte[] contextBytes = new byte[maxLen];
            //这里也会被阻塞，直到有数据准备好
            int realLen = in.read(contextBytes, 0, maxLen);
            //读取信息
            String message = new String(contextBytes, 0, realLen);

            //下面打印信息
            System.out.println("服务器2收到来自于端口：" + sourcePort + "的信息：" + message);

            //下面开始发送信息
            out.write("回发响应信息！".getBytes());
            //关闭
            out.close();
            in.close();
            accept.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (this.accept != null) {
                    accept.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

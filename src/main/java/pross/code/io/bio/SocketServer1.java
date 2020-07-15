package pross.code.io.bio;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @describe: 服务器端（BIOSocketServer）单个线程处理
 * @author: 彭爽 pross.peng
 * @date: 2020/07/05
 */
public class SocketServer1 {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            serverSocket = new ServerSocket(8888);
            while (true) {
                System.out.println("启动服务，等待消息...");
                Socket accept = serverSocket.accept();
                //收取信息
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
                System.out.println("服务器收到来自于端口：" + sourcePort + "的信息：" + message);

                //回应信息
                if(StringUtils.equals(message,"exit")){
                    out.write("正在关闭服务端！".getBytes());
                    out.flush();
                    break;
                }else{
                    out.write("回发响应信息！".getBytes());
                    out.flush();
                    System.out.println("已回复消息！");
                }
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if(serverSocket!=null){
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

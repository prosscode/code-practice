package code.java.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @describe: 阻塞客户端
 * @author: 彭爽 pross.peng
 * @date: 2020/07/05
 */
public class SocketClient1 {


    public static void receiveMessage(InputStream clientResponse) throws IOException {
        int maxLen = 1024;
        byte[] contextBytes = new byte[maxLen];
        int realLen;
        String message = "";
        //程序执行到这里，会一直等待服务器返回信息（in和out都不能close，如果close了就收不到服务器返回的消息）
        while((realLen = clientResponse.read(contextBytes, 0, maxLen)) != -1) {
            message += new String(contextBytes, 0, realLen);
        }
        System.out.println("接收到来自服务器的信息：" + message);
    }

    public static void sendMessage(OutputStream clientRequest,String message) throws IOException {
        System.out.println("客户端发送消息："+message);
        clientRequest.write(message.getBytes());
        clientRequest.flush();
        System.out.println("客户端的请求发送完成，等待服务器返回信息");
    }


    public static void main(String[] args) {
        Socket socket = null;
        OutputStream clientRequest = null;
        InputStream clientResponse = null;

        try {
            socket = new Socket("localhost",8888);
            clientRequest = socket.getOutputStream();
            clientResponse = socket.getInputStream();

            // 第一次请求 发送请求信息
            sendMessage(clientRequest,"我要请求服务器\n");
//            receiveMessage(clientResponse);
            // 第二次请求 over
            sendMessage(clientRequest,"第二次请求服务器 over");
            receiveMessage(clientResponse);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientResponse != null) {
                    clientResponse.close();
                }
                if (clientRequest != null) {
                    clientRequest.close();
                }
                if(socket!=null){
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

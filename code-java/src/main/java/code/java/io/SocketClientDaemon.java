package code.java.io;

import java.util.concurrent.CountDownLatch;

/**
 * @describe:
 *      模拟20个客户端并发请求
 * @author: 彭爽 pross.peng
 * @date: 2020/07/05
 */
public class SocketClientDaemon {

    /**
     * 同步io：阻塞/非阻塞/多路复用
     *  只有上层（包括上层的某种代理机制）系统询问我是否有某个事件发生了，否则我不会主动告诉上层系统事件发生了：
     * @param args
     * @throws InterruptedException
     */

    public static void main(String[] args) throws InterruptedException {
        int clientNumber = 20;
        CountDownLatch countDownLatch = new CountDownLatch(clientNumber);
        // 分别开始启动20个线程
        for (int i = 0; i < clientNumber; i++,countDownLatch.countDown()) {
            SocketClientRequestThread client = new SocketClientRequestThread(countDownLatch, i);
            new Thread(client).start();
        }

        //这个wait不涉及到具体的实验逻辑，只是为了保证守护线程在启动所有线程后，进入等待状态
        synchronized (SocketClientDaemon.class) {
            SocketClientDaemon.class.wait();
        }
    }
}

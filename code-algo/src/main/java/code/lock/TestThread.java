package code.lock;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * @date 2022/2/17
 * @created by shuang.peng
 * @description TestThread
 */
public class TestThread implements Runnable{

    private Integer threadFlag;
    private InterProcessMutex lock;

    public TestThread(Integer threadFlag, InterProcessMutex lock) {
        this.threadFlag = threadFlag;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            lock.acquire();
            System.out.println("第" + threadFlag + "线程获取到了锁");
            //等到1秒后释放锁
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

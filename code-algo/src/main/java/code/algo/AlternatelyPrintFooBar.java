package code.algo;

import java.util.concurrent.Semaphore;

/**
 * @Date 2024/6/18
 * @Created by Shuang
 * @Description AlternatelyPrintFooBar
 */
public class AlternatelyPrintFooBar {

    // Semaphore（信号量）是用于控制多个线程之间访问共享资源的并发工具。
    // Semaphore维护了一个计数器，表示当前可用的资源数量，线程可以通过获取或释放信号量来访问或释放共享资源。
    Semaphore s1, s2;

    private int n;

    public AlternatelyPrintFooBar(int n) {
        this.n = n;
        s1 = new Semaphore(1);
        s2 = new Semaphore(0);
    }

    public void foo(Runnable printFoo) throws InterruptedException {

        for (int i = 0; i < n; i++) {
            s1.acquire();
            // printFoo.run() outputs "foo". Do not change or remove this line.
//            printFoo.run();
            System.out.print("foo");
            s2.release();
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            s2.acquire();
            // printBar.run() outputs "bar". Do not change or remove this line.
//            printBar.run();
            System.out.println("bar");
            s1.release();
        }
    }

    public static void main(String[] args) {
        AlternatelyPrintFooBar printFooBar = new AlternatelyPrintFooBar(6);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printFooBar.foo(this);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printFooBar.bar(this);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}

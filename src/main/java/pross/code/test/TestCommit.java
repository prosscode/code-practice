package pross.code.test;

/**
 * @describe:
 * @author: 彭爽 pross.peng
 * @date: 2020/08/23
 */
public class TestCommit {

    public static void f() {
        String[] a = new String[2];
        Object[] b = a;
        a[0] = "hi";
        b[1] = Integer.valueOf(42).toString();
//        b[1] = "hey";

        for (Object o : b) {
            System.out.println(o);
        }

        System.out.println();

        for (String s : a) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) {
        TestCommit.f();
//        System.out.println("hello world");

    }
}

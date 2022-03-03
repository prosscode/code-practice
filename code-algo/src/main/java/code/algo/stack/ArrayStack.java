package code.algo.stack;


import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;

/**
 * @Date 2022/2/21
 * @Author by shuang.peng
 * @Description ArrayStack 用数组实现一个stack，顺序栈。
 * 栈的特点：后进者先出，先进者后出，只允许在一端插入和删除数据。
 */
public class ArrayStack {
    // 数组
    private String[] items;
    // 栈中元素个数
    private int count;
    //栈的大小
    private int n;

    // 初始化数组，申请一个大小为n的空间
    public ArrayStack(int n){
        this.items = new String[n];
        this.n = n;
        this.count = 0;
        Stack<Object> stack = new Stack<>();
        String valueOf = String.valueOf(12);
    }

    // 入栈操作
    public boolean push(String item){
        // 如果空间不够，则需要扩容
        if (n == count) {
            String[] newStr = new String[n * 2];
            System.arraycopy(items,0,newStr,0,items.length);
            items = newStr;
        }
        // 将item放到下标为count的位置，并且count+1
        items[count] = item;
        count ++;
        return true;
    }

    // 出栈操作
    public String pop(){
        if(count == 0){
            return null;
        }
        // 先进后出，后进先出
        count--;
        return items[count];
    }

    public static void main(String[] args) {
        ArrayStack stack = new ArrayStack(3);
        stack.push("hello");
        stack.push("world");
        stack.push("code");
        stack.push("java");
        System.out.println(Arrays.toString(stack.items));
        System.out.println(stack.pop());
        System.out.println(stack.pop());
    }

}

package code.algo;

import java.util.HashMap;
import java.util.Stack;

/**
 * @describe: 输入一个数组和一个数字，在数组中查找两个数，使得它们的和正好是输入的那个数字。
 * 要求时间复杂度是O(N)。如果有多对数字的和等于输入的数字，输出任意一对即可。
 * @author:  shuang.peng
 * @date: 2020/09/17
 */
public class TwoSumFindNum {


    public static void twoSum1(int[] a,int sum){
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            map.put(i,a[i]);
        }

        for (int i : a) {
            boolean value = map.containsValue(sum - i);
        }
    }

    public static void main(String[] args) {
        String str="hello  world  pross.";
        String[] split = str.split("\\s");
        for (int i = split.length-1; i >= 0; i--) {
            System.out.println(split[i]);
        }
        Stack<Object> stack = new Stack<>();


    }
}

package code.algo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @describe: 输入一个数组和一个数字，在数组中查找两个数，使得它们的和正好是输入的那个数字。
 * 要求时间复杂度是O(N)。如果有多对数字的和等于输入的数字，输出任意一对即可。
 * @author: 彭爽 pross.peng
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
//        int[] ints = new int[]{1,2,4,7,11,15};
//        int sum = 16;
//        twoSum1(ints,sum);

        HashMap<Object, Object> map = new HashMap<>();
        map.put(1,2);
        boolean value = map.containsValue(2);
        System.out.println(value);

    }
}

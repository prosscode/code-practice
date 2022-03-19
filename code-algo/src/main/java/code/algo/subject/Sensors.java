package code.algo.subject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2022/3/16
 * @Author by shuang.peng
 * @Description Subject
 *  有序数组，找出某个数字，首次出现的index
 *  如果不存在返回-1
 */
public class Sensors {

    static int findNumberIndex(List<Integer> numbers, int target) {
        int res = -1;
        int left = 0;
        int right = numbers.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (numbers.get(mid) > target) {
                left = mid + 1;
            } else if (numbers.get(mid) < target) {
                right = mid - 1;
            } else {
                right = mid - 1;
                res = mid;
            }
        }
        return res;
    }


    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(4);
        list.add(4);
        list.add(4);
        list.add(5);
        list.add(6);
        System.out.println(findNumberIndex(list,4));
    }
}

package code.algo.subject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2022/3/17
 * @Author by shuang.peng
 * @Description 数组去重，子数组分别去重
 */
public class Sangfor {

    static List<Integer> distinct(List<Integer> list){
        ArrayList<Integer> tmp = new ArrayList<>();
        tmp.add(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            if(list.get(i)==list.get(i-1)){
                continue;
            }else{
                tmp.add(list.get(i));
            }
        }
        return tmp;
    }

    static List<Integer> windowSet(ArrayList<Integer> list) {
        // 滑动窗口
        if (list.size() > 3) {
            // 控制左边界
            for (int i = 0; i < list.size(); i++) {
                // 控制窗口大小
                for (int j = 2; j < list.size() / 2;j++) {
                    // 比较是否重复
                }

            }
        } else {
            return list;
        }
        return null;
    }

    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(3);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(2);
        list.add(3);
        list.add(2);
        list.add(3);
        List<Integer> distinct = distinct(list);
        for (Integer integer : distinct) {
            System.out.println(integer);
        }
    }
}

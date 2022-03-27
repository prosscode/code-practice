package code.algo.subject;


/**
 * @Date 2022/3/17
 * @Author by shuang.peng
 * @Description 数组中的相邻元素或子数组去重
 */
public class Sangfor {

    static String distinct(String str){
        // 思路
        // 增加窗口大小，范围[0,list.size()/2]
        // 窗口划定后，前后比较子数组是否相同
        // 相等就切割
        for (int window = 1; window <= str.length() / 2; window++) {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            while (i + window < str.length()) {
                String s1 = str.substring(i, i + window);
                String s2 = str.substring(i + window, i + window * 2);
                if (s1.equals(s2)) {
                    i += window * 2;
                }else{
                    i += window;
                }
                builder.append(s1);
            }
            str=builder.toString();
        }
        return str;
    }

    public static void main(String[] args) {
        String str="12331232323";
        String distinct = distinct(str);
        System.out.println(distinct);
    }
}

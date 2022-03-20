package code.algo.slidingwindow;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2022/3/20
 * @Author by shuang.peng
 * @Description 给定一个字符串 s 和一个非空字符串 p，找到 s 中所有是 p 的字母异位词的子串，返回这些子串的起始索引。
 * 字母异位词: 字母必须要相同，索引位置可以不同
 */
public class FindAnagrams {

    static List<Integer> findAnagrams(List<Integer> res,String s,String p){
        char[] charp = p.toCharArray();
        ArrayList<String> listp = new ArrayList<>();
        for (char c : charp) {
            listp.add(String.valueOf(c));
        }
        // 固定窗口
        int windowSize = p.length();
        for (int i = 0; i < s.length() - windowSize; i++) {
            List<String> tmp = new ArrayList<>(listp);
            String sub = s.substring(i, i + windowSize);
            char[] subChar = sub.toCharArray();
            // 比较值
            for (char c : subChar) {
                // 如果是remove(char)，会把char的ASCII码当作index，出现out of index exception
                tmp.remove(String.valueOf(c));
            }
            if (tmp.size() == 0) {
                res.add(i);
            }
        }
        return res;
    }
    public static void main(String[] args) {
        String s="cbaebabacd";
        String p="abc";
        List<Integer> anagrams = new ArrayList<>();
        findAnagrams(anagrams,s,p);
        for (Integer anagram : anagrams) {
            System.out.println(anagram);
        }

    }

}

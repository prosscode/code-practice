package pross.code.leetcode;

/**
 * @describe: 字符串的排列
 * @author: 彭爽 pross.peng
 * @date: 2020/08/13
 */
public class CheckInclusion {

    public boolean checkInclusion(String s1, String s2) {
        if(s1.contains(s2)){
            return true;
        }else{
            return false;
        }

    }

}

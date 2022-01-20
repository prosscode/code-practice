package code.algo;

/**
 * @describe: 字符串的排列
 * @author:  shuang.peng
 * @date: 2020/08/13
 */
public class CheckInclusion {

    public static boolean checkInclusion(String s1, String s2) {
        if(s1.contains(s2)){
            return true;
        }else{
            return false;
        }
    }

    public static void main(String[] args) {
        boolean checkInclusion = checkInclusion("123", "2235");
    }

}

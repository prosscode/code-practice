package code.algo;

import org.junit.Test;
import scala.Int;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2022/2/19
 * @Author by shuang.peng
 * @Description Subject
 */
public class Subject {


    /**
     * 给一串字符串，拆分成5段，每段小于500，列出所有的情况
     */
    @Test
    public void getSplitStrAndCompareReturnNumber(){
        String str= "123456789";
        int n = 1;
        String init = "";
        ArrayList<String> result = new ArrayList<>();
        getAllNums(str, n, result,init);
        for (String s : result) {
            System.out.println(s);
        }
    }

    public static void getAllNums(String s, int n, List<String> result, String init) {
        if (n > 5) {
            return;
        }
        int length = s.length();
        if (n < 5) {
            for (int i = 1; i <= 3; i++) {
                if (i <= length) {
                    String tmp = s.substring(0, i);
                    int anInt = Integer.parseInt(tmp);
                    if (anInt <= 500) {
                        String tmp1 = String.copyValueOf(init.toCharArray());
                        tmp1 += "," + anInt;
                        getAllNums(s.substring(i, length), n + 1, result, tmp1);
                    } else {
                        return;
                    }
                }
            }
        } else {
            int anInt = Integer.parseInt(s);
            if (anInt <= 500) {
                String tmp1 = String.copyValueOf(init.toCharArray());
                tmp1 += "," + anInt;
                result.add(tmp1.replaceFirst(",", ""));
            } else {
                return;
            }
        }
        return;
    }

}

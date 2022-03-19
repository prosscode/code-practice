package code.algo.subject;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2022/2/19
 * @Author by shuang.peng
 * @Description 给一串字符串，拆分成5段，每段小于500，列出所有的情况
 */
public class ByteSafe {

    @Test
    public void getSplitStrAndCompareReturnNumber(){
        String str= "56346722512";
        int n = 1;
        String init = "";
        ArrayList<String> result = new ArrayList<>();
        getAllNums(str, n, result,init);
        for (String s : result) {
            System.out.println(s);
        }
    }

    public static void getAllNums(String str, int n, List<String> result, String init) {
        if(n>5) {
            return;
        } else if (n < 5) {
            int length = str.length();
            for (int i = 1; i <= 3; i++) {
                String substring = str.substring(0, i);
                int anInt = Integer.parseInt(substring);
                if (anInt <= 500) {
                    String tmp = init;
                    tmp += "," + anInt;
                    getAllNums(str.substring(i, length), n + 1, result, tmp);
                } else {
                    return;
                }
            }
        } else {
            int anInt = Integer.parseInt(str);
            if(anInt <=500){
                String tmp = init;
                tmp+=","+anInt;
                System.out.println(tmp.replaceFirst(",",""));
            }else{
                return;
            }
        }

    }
}

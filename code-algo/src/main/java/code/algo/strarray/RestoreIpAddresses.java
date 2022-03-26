package code.algo.strarray;


import java.util.ArrayList;

/**
 * @describe:
 * @author:  shuang.peng
 * @date: 2020/08/10
 */
public class RestoreIpAddresses {
    /**
     * 给定一个只包含数字的字符串，复原它并返回所有可能的 IP 地址格式。
     * 有效的 IP 地址正好由四个整数（每个整数位于 0 到 255 之间组成），整数之间用 '.' 分隔。
     *
     * 输入: "25525511135"
     * 输出: ["255.255.11.135", "255.255.111.35"]
     *
     */
    public static ArrayList<String> res;

    public void restoreIpAddresses(String str, int seg, String init) {
        if (seg > 3) {
            return;
        } else if (seg < 3) {
            int length = str.length();
            for (int i = 1; i <= 3; i++) {
                if (i > str.length()) return;
                String substring = str.substring(0, i);
                int anInt = Integer.parseInt(substring);
                if (anInt <= 255 && substring.charAt(0)!='0') {
                    String tmp = init;
                    tmp += "." + substring;
                    restoreIpAddresses(str.substring(i,length), seg + 1, tmp);
                } else {
                    return;
                }
            }
        } else {
            if (str.length() == 0) return;
            int anInt = Integer.parseInt(str);
            if (anInt <= 255 && str.charAt(0) != '0') {
                String tmp = init;
                tmp += "." + str;
                res.add(tmp.replaceFirst(".",""));
            }
        }
    }

    public static void main(String[] args) {
        RestoreIpAddresses addresses = new RestoreIpAddresses();
        res = new ArrayList<>();
        addresses.restoreIpAddresses("25525511135", 0, "");
        for (String re : res) {
            System.out.println(re);
        }

        String join = String.join(".", "0000");
        System.out.println(join);
    }
}

package code.algo;

import java.util.ArrayList;
import java.util.List;

/**
 * @describe:
 * @author:  shuang.peng
 * @date: 2020/08/10
 */
public class RestoreIpAddresses {
    /**
     * 给定一个只包含数字的字符串，复原它并返回所有可能的 IP 地址格式。
     *
     * 有效的 IP 地址正好由四个整数（每个整数位于 0 到 255 之间组成），整数之间用 '.' 分隔。
     *
     * 输入: "25525511135"
     * 输出: ["255.255.11.135", "255.255.111.35"]
     *
     */

    public List<String> restoreIpAddresses(String s) {
        ArrayList<String> list = new ArrayList<>();
        return list;
    }

    public static void main(String[] args) {
        RestoreIpAddresses addresses = new RestoreIpAddresses();
        addresses.restoreIpAddresses("123456789");
    }
}

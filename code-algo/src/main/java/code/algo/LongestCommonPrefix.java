package code.algo;

/**
 * @describe: 最长公共前缀
 * @author: 彭爽 pross.peng
 * @date: 2020/08/12
 */
public class LongestCommonPrefix {

    /**
     * 编写一个函数来查找字符串数组中的最长公共前缀。
     *
     * 如果不存在公共前缀，返回空字符串 ""。
     *
     * 示例 1:
     *
     * 输入: ["flower","flow","flight"]
     * 输出: "fl"
     * 示例 2:
     *
     * 输入: ["dog","racecar","car"]
     * 输出: ""
     * 解释: 输入不存在公共前缀。
     * 说明:
     *
     * 所有输入只包含小写字母 a-z 。
     * @param strs
     * @return
     */

    public static String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        if(strs.length==1){
            return strs[0];
        }
        String first = strs[0];
        String prefix ="";
        int count = strs.length;
        for (int i = 1; i < count; i++) {
            int length = Math.min(first.length(), strs[i].length());
            System.out.println(length);
            int index = 0;
            while (index < length && first.charAt(index) == strs[i].charAt(index)) {
                index++;
                prefix = first.substring(0, index);
            }

            if (prefix.length() == 0) {
                break;
            }
        }
        return prefix;
    }

    public static void main(String[] args) {
        String prefix = LongestCommonPrefix.longestCommonPrefix(new String[]{"abcdef", "abcr", "abyhopjhjbn"});
        System.out.println(prefix);
    }

}

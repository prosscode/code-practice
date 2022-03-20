package code.algo.slidingwindow;

import java.util.HashMap;

/**
 * @Date 2022/3/19
 * @Author by shuang.peng
 * @Description 滑动窗口解决 (minimum-window-substring)
 * 给定一个字符串 S 和一个字符串 T，请在 S 中找出包含 T 所有字母的最小子串
 */
public class MinWindow {

    /**
     * 思路：
     * 1。遍历字符串S，如果在字符串T中存在，则为窗口开始，确定左边界；
     * 2。窗口右边界开始右移，直到匹配字符串T所有的字符，此为第一个匹配的窗口大小；
     * 3。窗口左边界开始左移，如果缩小之后不能满足包含字符串T，则停止左移，继续右移
     * 4。直到遍历完整个字符串S。
     * @param raw 字符串S
     * @param target 字符串T
     * @return
     */
    static String minWindowStr(String raw, String target) {
        char[] chars = raw.toCharArray();
        char[] chart = target.toCharArray();
        HashMap<Character, Integer> maps = new HashMap<>();
        HashMap<Character, Integer> mapt = new HashMap<>();
        for (char c : chart) {
            if (mapt.containsKey(c)) {
                mapt.put(c, mapt.get(c) + 1);
            } else {
                mapt.put(c, 1);
            }
        }

        // 窗口左右指针
        int left = 0, right = 0;
        // 最小覆盖开始位置，最小覆盖长度
        int start = 0, len = Integer.MAX_VALUE;
        // 更新指针的判断条件
        int valid = 0;
        // 滑动窗口开始遍历字符串S
        while (right < chars.length) {
            char ch = chars[right++];
            // 字符串S加入maps
            if (maps.containsKey(ch)) {
                maps.put(ch, maps.get(ch) + 1);
            } else {
                maps.put(ch, 1);
            }
            // valid和mapt的size判断是否完全包含字符串T
            if(maps.get(ch).equals(mapt.get(ch))){
                valid++;
            }
            // 如果全部包含，表示已经找到第一个包含字符串S的字串
            // left开始左移，继续寻找
            while (valid == mapt.size()) {
                if (right - left < len) {
                    start = left;
                    len = right - left;
                }
                // left左移
                char leftc = chars[left++];
                maps.put(leftc, maps.get(leftc) - 1);
                if (mapt.containsKey(leftc) && maps.get(leftc) < mapt.get(leftc)) {
                    valid--;
                }
            }
        }

        return len == Integer.MAX_VALUE ? "" : raw.substring(start, start + len);
    }

    public static void main(String[] args) {
        String raw = "ADOBECODEBANC";
        String target = "ABC";
        System.out.println(minWindowStr(raw,target));
    }
}

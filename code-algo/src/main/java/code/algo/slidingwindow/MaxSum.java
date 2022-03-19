package code.algo.slidingwindow;

/**
 * @Date 2022/3/19
 * @Author by shuang.peng
 * @Description 滑动窗口
 * 给定一个整数数组，计算长度为 'k' 的连续子数组的最大总和。
 */
public class MaxSum {

    static int maxSum(int[] nums, int k) {
        int length = nums.length;
        if (k > length) {
            return -1;
        }
        // 计算第一个窗口的值
        int maxSum = 0;
        for (int i = 0; i < k; i++) {
            maxSum += nums[i];
        }

        int sum = maxSum;
        for (int i = k; i < length; i++) {
            // 当前窗口元素和 = 前一个窗口元素和 + 当前元素值 - 前一个窗口第一个元素值
            sum += nums[i] - nums[i - k];
            maxSum = Math.max(maxSum, sum);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        int k = 3;
        int[] nums = new int[]{200, 300, 400, 100, 500};
        System.out.println(maxSum(nums, k));
    }
}

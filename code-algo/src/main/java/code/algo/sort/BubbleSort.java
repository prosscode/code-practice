package code.algo.sort;

/**
 * @date 2022/2/28
 * @created by shuang.peng
 * @description 冒泡排序 时间复杂度：O(n^2)
 * 冒泡排序只会操作相邻的两个数据。
 * 每次冒泡操作都会对相邻的两个元素进行比较，看是否满足大小关系要求。如果不满足就让它俩互换。
 * 一次冒泡会让至少一个元素移动到它应该在的位置，重复 n 次，就完成了 n 个数据的排序工作。
 * 优化点：
 * 如果元素比较后不互换位置，则表示该元素已经到了正确的位置，可以break掉后续的循环比较。
 */
public class BubbleSort {

    static int[] sort(int[] arr) {
        if (arr.length < 2) {
            return arr;
        }
        int length = arr.length;
        for (int i = 0; i < length; i++) {
            // 提前退出冒泡循环的标志位
            boolean flag = false;
            for (int j = 0; j < length - i - 1; j++) {
                // 比较
                if (arr[j] > arr[j + 1]) {
                    // 交换数据
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    // 表示有数据交换
                    flag = true;
                }
            }
            if (!flag) break;
        }
        return arr;
    }

    public static void main(String[] args) {
        int[] array = {1,3,5,7,9,2,4,6,10,2};
        int[] sort = sort(array);
        for (int i : sort) {
            System.out.println(i);
        }
    }
}

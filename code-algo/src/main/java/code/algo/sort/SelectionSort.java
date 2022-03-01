package code.algo.sort;

/**
 * @date 2022/2/28
 * @created by shuang.peng
 * @description SelectionSort, 时间复杂度为O(n^2)，不占用额外的内存空间。
 * 选择排序分已排序区间和未排序区间。
 * 和插入排序不同的是，选择排序每次会从未排序区间中找到最小的元素，将其放到已排序区间的末尾。
 */
public class SelectionSort {

    static int[] sort(int[] arr) {
        if (arr.length < 2) {
            return arr;
        }
        int length = arr.length;
        for (int i = 0; i < length - 1; i++) {
            int min = i;
            // 每轮需要比较的次数 length-i
            for (int j = i + 1; j < length; j++) {
                // 找出最小的值
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            // 将找到的最小值和i位置所在的值进行交换
            if (i != min) {
                int temp = arr[i];
                arr[i] = arr[min];
                arr[min] = temp;
            }
        }

        return arr;
    }

    public static void main(String[] args) {
        int[] array = {1, 3, 5, 7, 9, 2, 4, 6, 10, 2};
        int[] sort = sort(array);
        for (int i : sort) {
            System.out.println(i);
        }
    }
}

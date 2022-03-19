package code.algo.sort;

import java.util.Collections;
import java.util.List;

/**
 * @Date 2022/3/6
 * @Author by shuang.peng
 * @Description 快速排序, 平均时间复杂度为O(nlogn)，且常数因子n很小
 *
 * 1。从数列中挑出一个元素，称为 "基准"（pivot）;
 * 2。重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）
 *    在这个分区退出之后，该基准就处于数列的中间位置。这个称为分区（partition）操作；
 * 3。递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序
 *
 * 快排的处理过程是由上到下的，先分区，然后再处理子问题。
 */
public class QuickSort {

    private static int[] sort(int[] array) {
        int n = array.length;
        return quickSort(array, 0, n - 1);
    }

    // 快速排序递归函数，p,r为下标
    private static int[] quickSort(int[] arr, int left, int right) {
        if (left >= right) return arr;
        // 获取分区点pivot
        int pivot = partition(arr, left, right);
        // 递归
        quickSort(arr, left, pivot - 1);
        quickSort(arr, pivot + 1, right);
        return arr;
    }

    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left;
        for(int j = left; j < right; ++j) {
            // 如果遍历的值小于pivot，如果索引值不相等，则交换位置
            if (arr[j] < pivot) {
                if (i == j) {
                    ++i;
                } else {
                    swap(arr,i++,j);
                }
            }
        }
        swap(arr,i,right);
        return i;
    }

    // 元素交换
    private static void swap(int[] arr, int i, int index) {
        int temp = arr[i];
        arr[i] = arr[index];
        arr[index] = temp;
    }


    public static void main(String[] args) {
        int[] array = {1, 3, 5, 7, 9, 2, 4, 6, 10, 2};
        int[] sort = sort(array);
        for (int i : sort) {
            System.out.println(i);
        }
    }

}

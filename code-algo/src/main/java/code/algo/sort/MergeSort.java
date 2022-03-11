package code.algo.sort;

import java.util.Arrays;

/**
 * @date 2022/2/28
 * @created by shuang.peng
 * @description 归并排序，时间复杂度 O(n log n)
 *
 * 采用分治法:
 * 分割：递归地把当前序列平均分割成两半。
 * 集成：在保持元素顺序的同时将上一步得到的子序列集成到一起（归并）。
 *
 * 步骤：
 * 1.申请空间，使其大小为两个已经排序序列之和，该空间用来存放合并后的序列；
 * 2.设定两个指针，最初位置分别为两个已经排序序列的起始位置；
 * 3.比较两个指针所指向的元素，选择相对小的元素放入到合并空间，并移动指针到下一位置；
 * 4.重复步骤3直到某一指针达到序列尾；
 * 5.将另一序列剩下的所有元素直接复制到合并序列尾。
 *
 * 归并排序的处理过程是由下到上的，先处理子问题，然后再合并
 */
public class MergeSort {

    static int[] sort(int[] arr) {
        if (arr.length < 2) {
            return arr;
        }
        int middle = (int) Math.floor(arr.length / 2);
        int[] left = Arrays.copyOfRange(arr, 0, middle);
        int[] right = Arrays.copyOfRange(arr, middle, arr.length);
        System.out.println(left.length + "," + right.length);
        return merge(sort(left), sort(right));
    }

    static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int i = 0;

        while (left.length > 0 && right.length > 0) {
            if (left[0] <= right[0]) {
                result[i++] = left[0];
                left = Arrays.copyOfRange(left, 1, left.length);
            } else {
                result[i++] = right[0];
                right = Arrays.copyOfRange(right, 1, right.length);
            }
        }

        while (left.length > 0) {
            result[i++] = left[0];
            left = Arrays.copyOfRange(left, 1, left.length);
        }

        while (right.length > 0) {
            result[i++] = right[0];
            right = Arrays.copyOfRange(right, 1, right.length);
        }

        return result;
    }


    public static void main(String[] args) {
        int[] array = {1,3,5,7,9,2,4,6,10,2};
        int[] sort = sort(array);
        for (int i : sort) {
            System.out.println(i);
        }
    }
}

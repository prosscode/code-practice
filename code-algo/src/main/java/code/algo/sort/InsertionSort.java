package code.algo.sort;

/**
 * @date 2022/2/28
 * @created by shuang.peng
 * @description 插入排序，
 * 将数组中的数据分为两个区间，已排序区间和未排序区间。
 * 初始已排序区间只有一个元素，就是数组的第一个元素。
 * 插入算法的核心思想是取未排序区间中的元素，在已排序区间中找到合适的插入位置将其插入，并保证已排序区间数据一直有序。
 * 重复这个过程，直到未排序区间中元素为空，算法结束。
 */
public class InsertionSort {

    static int[] sort(int[] arr) {
        if (arr.length < 2){
            return arr;
        }
        int length = arr.length;
        // 从第二个元素开始，第一个元素为初始区间有序
        for (int i = 1; i < length; i++) {
            int value = arr[i];
            // 需要比较的次数
            int j = i - 1;
            // 查找插入的位置
            for (; j >= 0; j--) {
                if (arr[j] > value) {
                    // 数据移动
                    arr[j + 1] = arr[j];
                } else {
                    break;
                }
            }
            // 插入数据
            arr[j + 1] = value;
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

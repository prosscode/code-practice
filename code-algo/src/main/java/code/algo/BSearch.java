package code.algo;


/**
 * @date 2022/2/18
 * @created by shuang.peng
 * @description 二分查找
 */
public class BSearch {

    // 循环实现
    int bSearch(int[] a, int value) {
        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            // 如果low+high数据较大，会溢出
            // int mid = (low + high) / 2; = low/2 + high /2
            int mid = low + (high - low) / 2;

            // value不匹配时，low/high=mid，可能会发生死循环
            // 当high=3，low=3 时，如果 a[3]不等于 value，就会导致一直循环不退出。
            if (a[mid] == value) {
                return mid;
            } else if (a[mid] < value) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    // 递归实现
    static int bsearchInternally(int[] a, int low, int high, int value) {
        if(low > high){
            return -1;
        }
        int mid = low + (high - low) / 2;
        if (low <= high) {
            if (a[mid] == value) {
                return mid;
            } else if (a[mid] < value) {
                return bsearchInternally(a, mid + 1, high, value);
            } else {
                return bsearchInternally(a, low, mid - 1, value);
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] a = new int[]{1,2,3,4,5,6,7,8,9};
        int i = bsearchInternally(a, 0, a.length, 3);
        System.out.println(i);
    }

}

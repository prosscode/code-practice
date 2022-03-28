package code.algo.subject;

/**
 * @date 2022/3/28
 * @created by shuang.peng
 * @description 旋转重复数组，找到下标最小的target值的索引
 */
public class ByteFeature {

    public static void main(String[] args) {
        System.out.println("hello world");
        // 二分查找
        int target=0;
        int[] nums=new int[]{2,5,6,7,7,7,8,0,0,0,0,1};
        int low=0;
        int high=nums.length-1;
        while(low<high){
            int mid=(low+high +1)/2;
            if(nums[mid]>=nums[0]){
                low=mid;
            }else{
                high=mid-1;
            }
        }
        // 确定升序空间
        if(target>=nums[0]){
            low=0;
        }else{
            low=high+1;
            high=nums.length-1;
        }

        int res=-1;
        while(low < high){
            int mid = low + (high-low)/2;
            if(nums[mid]>target){
                high=mid-1;
            }else if(nums[mid]<target){
                low=mid+1;
            }else{
                high=mid-1;
                res=mid;
            }
        }
        System.out.println(res);
    }

}

package code.algo;

import java.util.ArrayList;

/**
 * @describe: 从尾到头打印链表
 * 输入 head = [1,3,2]
 * 输出 [2,3,1]
 * @author:  shuang.peng
 * @date: 2020/09/16
 */
public class ResverseNodePrint {

    public int[] resversePrint(ListNode head){
        ArrayList<Integer> list = new ArrayList<>();
        while(head!=null){
            list.add(head.val());
            head = head.next();
        }
        int[] res = new int[list.size()];

        for (int i = 0; i < res.length; i++) {
            res[i] = list.get(list.size() - i - 1);
        }
        return res;
    }
}

class ListNode{

    public static int val(){
        return 0;
    }

    public static ListNode next(){
        return new ListNode();
    }
}

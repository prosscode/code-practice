package code.algo.linkedlist;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @describe: 从尾到头打印链表
 * 输入 head = [1,3,2]
 * 输出 [2,3,1]
 * @author:  shuang.peng
 * @date: 2020/09/16
 */
public class ReverseNode {

    // list保存，res反序输出
    public int[] reversePrint(ListNode head){
        ArrayList<Integer> list = new ArrayList<>();
        while(head!=null){
            list.add(head.val);
            head = head.next;
        }
        int[] res = new int[list.size()];

        for (int i = 0; i < res.length; i++) {
            res[i] = list.get(list.size() - i - 1);
        }
        return res;
    }

    // stack保存
    public static void reverse(ListNode head){
        Stack<Object> stack = new Stack<>();
        while (head != null) {
            stack.push(head.val);
            head = head.next;
        }

        while(!stack.empty()){
            System.out.println(stack.pop());
        }
    }

    public static void main(String[] args) {
        ListNode node1 = new ListNode(4, null);
        ListNode node2 = new ListNode(3, node1);
        ListNode node3 = new ListNode(2, node2);
        ListNode node4 = new ListNode(1, node3);
        reverse(node4);
    }

}



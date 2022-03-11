package code.algo.linkedlist;

import code.algo.linkedlist.ListNode;

import java.util.Stack;

/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class ReverseReturnNode {

    public static ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        Stack<ListNode> stack = new Stack<>();
        while (head != null) {
            stack.push(head);
            head = head.next;
        }
        ListNode newNode = stack.pop();
        ListNode tailNode = newNode;
        while (!stack.empty()) {
            ListNode node = stack.pop();
            tailNode.next = node;
            tailNode = node;
        }
        tailNode.next = null;
        return newNode;
    }

    public static void main(String[] args) {
        ListNode node1 = new ListNode(5, null);
        ListNode node2 = new ListNode(4, node1);
        ListNode node3 = new ListNode(2, node2);
        ListNode node4 = new ListNode(1, node3);
        ListNode listNode = reverseList(node4);
        while (listNode!=null){
            System.out.println(listNode.val);
            listNode = listNode.next;
        }
    }

}
package code.algo.linkedlist;


/**
 * @Date 2022/3/3
 * @Author by shuang.peng
 * @Description 排序两个有序链表
 */
public class MergeSortLinkedList {

    static ListNode mergeLinkedList(ListNode node1, ListNode node2) {
        if (node1 == null) {
            return node2;
        }
        if (node2 == null) {
            return node1;
        }
        ListNode res = node1.val < node2.val ? node1 : node2;
        res.next = mergeLinkedList(res.next, node1.val >= node2.val ? node1 : node2);
        return res;
    }



    public static void main(String[] args) {

        ListNode node1 = new ListNode(5, null);
        ListNode node2 = new ListNode(4, node1);
        ListNode node3 = new ListNode(2, node2);
        ListNode node4 = new ListNode(1, node3);

        ListNode list1 = new ListNode(7, null);
        ListNode list2 = new ListNode(6, list1);
        ListNode list3 = new ListNode(3, list2);
        ListNode list4 = new ListNode(1, list3);

        ListNode sortLinkedList = mergeLinkedList(list4, node4);
        while(sortLinkedList!=null){
            System.out.println(sortLinkedList.val);
            sortLinkedList = sortLinkedList.next;
        }
    }
}

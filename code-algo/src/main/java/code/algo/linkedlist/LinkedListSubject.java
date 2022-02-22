package code.algo.linkedlist;

import org.junit.Test;

/**
 * @Date 2022/2/20
 * @Author by shuang.peng
 * @Description LinkedListSubject
 */
public class LinkedListSubject {

    // 链表合并排序
    @Test
    public void mergeLinkedList(){
        LinkedList l7 = new LinkedList(3, null);
        LinkedList l6 = new LinkedList(4, l7);
        LinkedList l5 = new LinkedList(5, l6);
        LinkedList l4 = new LinkedList(6, l5);
        LinkedList l3 = new LinkedList(7, l4);
        LinkedList l2 = new LinkedList(8, l3);
        LinkedList l1 = new LinkedList(9, l2);


        LinkedList s5 = new LinkedList(5, null);
        LinkedList s4 = new LinkedList(4, s5);
        LinkedList s3 = new LinkedList(3, s4);
        LinkedList s2 = new LinkedList(2, s3);
        LinkedList s1 = new LinkedList(1, s2);


    }



    class LinkedList{
        int val;
        LinkedList link;

        public LinkedList(int val, LinkedList link) {
            this.val = val;
            this.link = link;
        }
    }
}

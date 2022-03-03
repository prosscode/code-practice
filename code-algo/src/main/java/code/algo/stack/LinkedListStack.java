package code.algo.stack;

/**
 * @Date 2022/2/21
 * @Author by shuang.peng
 * @Description LinkedListStack 链表栈，链式栈
 */
public class LinkedListStack {
    private LinkedListNode top;

    // 入栈操作
    public boolean push(String item){
        LinkedListNode node = new LinkedListNode(item, null);
        if(top == null){
            top = node;
        }else{
            node.next = top;
        }
        return true;
    }

    // 出栈操作
    public String pop(){
        if(top == null){
            return null;
        }
        String data = top.val;
        top = top.next;
        return data;
    }

    public void printAll() {
        LinkedListNode p = top;
        while (p != null) {
            System.out.print(p.val + " ");
            p = p.next;
        }
    }

    class LinkedListNode{
        String val;
        LinkedListNode next;

        public LinkedListNode(String val, LinkedListNode next) {
            this.val = val;
            this.next = next;
        }
    }
}

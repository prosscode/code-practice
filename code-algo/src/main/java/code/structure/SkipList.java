package code.structure;

import java.util.Random;

/**
 * @date 2022/2/10
 * @created by shuang.peng
 * @description SkipList跳表，通过建立多层索引，实现二分查找的有序链表。
 * 每次增加、删除元素都需要重建索引。
 */
public class SkipList {
    // 最高索引层数
    private final static int MAX_LEVEL = 16;
    private int levelCount = 1;
    // 带头链表
    private Node head = new Node(MAX_LEVEL);
    private static Random r = new Random();

    /**
     * 插入元素
     * @param value
     */
    public void insert(int value) {
        int level = head.forwards[0] == null ? 1 : randomLevel();
        // 新增加的node
        Node newNode = new Node(level);
        newNode.data = value;
        newNode.maxLevel = level;
        Node[] update = new Node[level];
        for (int i = 0; i < level; ++i) {
            // 初始化每一层链表的head
            update[i] = head;
        }

        // 从最大索引层开始查找，找到前一节点，通过--i，移动到下层再开始查找
        Node p = head;
        for (int i = level - 1; i >= 0; i--) {
            while (p.forwards[i] != null && p.forwards[i].data < value) {
                // 找到前一节点
                p = p.forwards[i];
            }
            // levelCount 会 > level，所以需要加上判断
            if (level > i) {
                update[i] = p;
            }
        }

        for (int i = 0; i < level; i++) {
            newNode.forwards[i] = update[i].forwards[i];
            update[i].forwards[i] = newNode;
        }
    }

    /**
     * 理论来讲，一级索引中元素个数应该占原始数据的 50%，二级索引中元素个数占 25%，三级索引12.5% ，一直到最顶层。
     * 因为每一层的晋升概率是 50%。对于每一个新插入的节点，都需要调用 randomLevel 生成一个合理的层数。
     * randomLevel 方法会随机生成 1~MAX_LEVEL 之间的数，且 ：
     *  50%的概率返回 1
     *  25%的概率返回 2
     *  12.5%的概率返回 3 ...
     * @return
     */
    private static int randomLevel() {
//        int level = 1;
//        while (Math.random() < 0.5f && level < MAX_LEVEL){
//            level ++;
//        }
//        return level;

        int level = 1;
        for (int i = 1; i < MAX_LEVEL; ++i) {
            // 如果是奇数层数 +1，防止伪随机
            if (r.nextInt() % 2 == 1) {
                level++;
            }
        }
        return level;
    }

    /**
     * 打印所有数据
     */
    public void printAll() {
        Node p = head;
        Node[] c = p.forwards;
        Node[] d = c;
        int maxLevel = c.length;
        for (int i = maxLevel - 1; i >= 0; i--) {
            System.out.print(i + ":");
            do {
                System.out.print((d[i] != null ? d[i].data : "null") + ",");
            } while (d[i] != null && (d = d[i].forwards)[i] != null);
            System.out.println();
            d = c;
        }
    }

    public static void main(String[] args) {
        SkipList list = new SkipList();
        list.insert(1);
        list.insert(2);
        list.insert(6);
        list.insert(7);
        list.insert(8);
        list.insert(3);
        list.insert(4);
        list.insert(5);
        list.printAll();
    }


    public class Node {
        private int data = -1;
        // 表示当前节点位置的下一个节点所有层的数据，从上层切换到下层，就是数组下标-1，
        // forwards[3]表示当前节点在第三层的下一个节点
        private Node forwards[];
        private int maxLevel = 0;
        public Node(int level) {
            forwards = new Node[level];
        }
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{data:");
            builder.append(data);
            builder.append(";levels:");
            builder.append(maxLevel);
            builder.append("}");
            return builder.toString();
        }
    }
}

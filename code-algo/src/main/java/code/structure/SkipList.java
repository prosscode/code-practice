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
    private final static int MAX_LEVEL = 8;
    private int levelCount = 1;
    // 带头链表
    private Node head = new Node(MAX_LEVEL);
    private static Random r = new Random();

    // 查找元素
    public Node find(int value) {
        Node p = head;
        // 从最大层开始查找，找到前一节点，通过--i，移动到下层再开始查找
        for (int i = levelCount - 1; i >= 0; --i) {
            while (p.forwards[i] != null && p.forwards[i].data < value) {
                // 找到前一节点
                p = p.forwards[i];
            }
        }

        if (p.forwards[0] != null && p.forwards[0].data == value) {
            return p.forwards[0];
        } else {
            return null;
        }
    }


    /**
     * 插入元素
     * @param value 插入元素值
     */
    public void insert(int value) {
        int level = 0;
        // 随机一个层数
        if (level == 0) {
            level = randomLevel();
        }
        // 创建新节点
        Node newNode = new Node(level);
        newNode.data = value;
        // 表示从最大层到低层，都要有节点数据
        newNode.maxLevel = level;
        // 记录要更新的层数，表示新节点要更新到哪几层
        Node update[] = new Node[level];
        for (int i = 0; i < level; ++i) {
            update[i] = head;
        }

        /**
         * 1，说明：层是从下到上的，这里最下层编号是0，最上层编号是7
         * 2，这里没有从已有数据最大层（编号最大）开始找，（而是随机层的最大层）导致有些问题。
         *    如果数据量为1亿，随机level=1 ，那么插入时间复杂度为O（n）
         */
        Node p = head;
        for (int i = level - 1; i >= 0; --i) {
            while (p.forwards[i] != null && p.forwards[i].data < value) {
                p = p.forwards[i];
            }
            // 这里update[i]表示当前层节点的前一节点，因为要找到前一节点，才好插入数据
            update[i] = p;
        }

        // 将每一层节点和后面节点关联
        for (int i = 0; i < level; ++i) {
            // 记录当前层节点后面节点指针
            newNode.forwards[i] = update[i].forwards[i];
            // 前一个节点的指针，指向当前节点
            update[i].forwards[i] = newNode;
        }

        // 更新层高
        if (levelCount < level) levelCount = level;
    }


    /**
     * 插入元素2
     * @param value 插入元素值
     */
    public void insert2(int value) {
        int level = head.forwards[0] == null ? 1 : randomLevel();
        // 每次只增加一层，levelCount++
        if (level > levelCount) {
            level = levelCount++;
        }
        Node newNode = new Node(level);
        newNode.data = value;
        Node p = head;
        // 从最大层开始查找，找到前一节点，通过--i，移动到下层再开始查找
        for (int i = levelCount - 1; i >= 0; --i) {
            while (p.forwards[i] != null && p.forwards[i].data < value) {
                // 当层往后找，找到value位置的前一节点
                p = p.forwards[i];
            }
            // levelCount 会 > level，所以加上判断
            if (level > i) {
                if (p.forwards[i] == null) {
                    p.forwards[i] = newNode;
                } else {
                    Node next = p.forwards[i];
                    p.forwards[i] = newNode;
                    newNode.forwards[i] = next;
                }
            }
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
        SkipList skipList = new SkipList();
        skipList.insert2(2);
        skipList.insert2(6);
        skipList.insert2(7);
        skipList.insert2(8);
        skipList.insert2(3);
        skipList.insert2(4);
        skipList.insert2(5);
        skipList.insert2(15);
        skipList.insert2(25);
        skipList.insert2(35);
        skipList.insert2(45);
        skipList.insert2(55);
        skipList.insert2(65);
        skipList.printAll();
        Node node = skipList.find(15);
        System.out.println(node.toString());
    }


    public class Node {
        private int data = -1;
        // 表示当前节点位置的下一个节点所有层的数据，从上层切换到下层，就是数组下标-1，
        // forwards[3]表示当前节点在第三层的下一个节点
        private Node[] forwards;
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

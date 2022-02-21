package code.algo.tree;

import org.junit.Test;

/**
 * @Date 2022/2/20
 * @Author by shuang.peng
 * @Description BinarySearchTree 二叉查找树，又称二叉搜索树，
 * 支持快速查找一个数据和快速插入、删除一个数据。
 * 要求：
 * 在树中的任意一个节点，其左子树中的每个节点的值，都要小于这个节点的值，而右子树节点的值都大于这个节点的值。
 */
public class BinarySearchTree {

    // 快速查找一个数据
    @Test
    public void findNumber() {
        int value = 3;
        TreeNode leftNode = new TreeNode(2, null, null);
        TreeNode rightNode = new TreeNode(3, null, null);
        TreeNode root = new TreeNode(1, leftNode, rightNode);

        while (root != null) {
            int val = root.val;
            if (val > value) {
                root = root.left;
            } else if (val < value) {
                root = root.right;
            } else {
                System.out.println("done");
                return;
            }
        }
    }


    // 插入节点
    @Test
    public void insertNumber() {
        TreeNode leftNode = new TreeNode(2, null, null);
        TreeNode rightNode = new TreeNode(3, null, null);
        TreeNode root = new TreeNode(1, leftNode, rightNode);

        // 需要插入的node
        TreeNode newNode = new TreeNode(4, null, null);
        while (root != null) {
            if (root.val > newNode.val) {
                if (root.left == null) {
                    root.left = newNode;
                    return;
                }
                root = root.left;
            } else if (root.val < newNode.val) {
                if (root.right == null) {
                    root.right = newNode;
                    return;
                }
                root = root.right;
            }
        }
        System.out.println("done");
    }

    /**
     * 删除操作，比较复杂
     * 1。如果删除的节点，没有子节点，那么直接值为null
     * 2。如果删除的节点，只有一个子节点（左节点或者右节点），直接让父节点指向被删除的子节点
     * 3。如果删除的节点，左右子节点都在，就需要找到节点中右子节点中的最小的节点，让父节点指向它
     */
    @Test
    public void deleteNumber() {
        TreeNode right5Node = new TreeNode(7, null, null);
        TreeNode right4Node = new TreeNode(6, null, right5Node);
        TreeNode left4Node = new TreeNode(4, null, right4Node);
        TreeNode right2Node = new TreeNode(5, left4Node, null);
        TreeNode leftNode = new TreeNode(1, null, null);
        TreeNode rightNode = new TreeNode(3, null, right2Node);
        TreeNode root = new TreeNode(2, leftNode, rightNode);
//        TreeNode root = new TreeNode(0, null, null);
        // 先找到要删除的节点
        TreeNode deleteNode = new TreeNode(3,null,null);
        // 查找的节点
        TreeNode node = root;
        // 查找节点的父节点
        TreeNode pNode = null;
        while (node != null && node.val != deleteNode.val) {
            pNode = node;
            if (node.val > deleteNode.val) {
                node = node.left;
            } else if (node.val < deleteNode.val) {
                node = node.right;
            }
        }

        if(node == null){
            return;
        }

        // 要删除的节点有两个子节点
        if (node.left != null && node.right != null) {
            // 找到节点的右子节点中的最小节点
            TreeNode minNode = node.right;
            TreeNode pMinNode = node;
            while (minNode.left != null) {
                minNode = minNode.left;
                pMinNode = minNode;
            }
            // 找到了就替换值
            node.val = minNode.val;
            // 指向其它对象
            node = minNode;
            pNode = pMinNode;
        }

        // 要删除的节点只有一个节点
        // 可能是node只有一个子节点，可能是minNode节点的子节点(minNode只有右子节点)
        TreeNode child;
        if (node.left != null) {
            child = node.left;
        } else if (node.right != null) {
            child = node.right;
        } else {
            child = null;
        }


        if (pNode == null) {
            // 删除的是根节点
            root = child;
        } else if (pNode.left == node) {
            pNode.left = child;
        } else {
            pNode.right = child;
        }

        BinaryTree.getPreNode(root);
        System.out.println("===");
        BinaryTree.getPreNode(pNode);

    }

    // 查找最大节点和最小节点
    @Test
    public void findMaxOrMinNode() {
        TreeNode right3Node = new TreeNode(6, null, null);
        TreeNode left4Node = new TreeNode(4, null, null);
        TreeNode right2Node = new TreeNode(5, left4Node, right3Node);
        TreeNode leftNode = new TreeNode(1, null, null);
        TreeNode rightNode = new TreeNode(3, null, right2Node);
        TreeNode root = new TreeNode(2, leftNode, rightNode);

        TreeNode node = root;
        while (node.left != null) {
            node = node.left;
        }
        System.out.println("min node value: "+node.val);

        TreeNode node1 = root;
        while (node1.right != null) {
            node1 = node1.right;
        }
        System.out.println("max node value: "+node1.val);
    }


}

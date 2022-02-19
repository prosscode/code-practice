package code.algo.tree;

import org.junit.Test;

/**
 * @Date 2022/2/20
 * @Author by shuang.peng
 * @Description BinarySearchTree
 * 二叉查找树，又称二叉搜索树，
 * 为了实现快速查找，支持快速查找一个数据和快速插入、删除一个数据。
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
        TreeNode right3Node = new TreeNode(6, null, null);
        TreeNode right2Node = new TreeNode(5, null, right3Node);
        TreeNode leftNode = new TreeNode(2, null, null);
        TreeNode rightNode = new TreeNode(3, null, right2Node);
        TreeNode root = new TreeNode(1, leftNode, rightNode);

        // 先找到要删除的节点
        TreeNode deleteNode = new TreeNode(5,null,null);
        // 查找的节点
        TreeNode node = null;
        // 查找的父节点
        TreeNode pNode = null;
        while (root != null) {
            pNode = root;
            if (root.val > deleteNode.val) {
                root = root.left;
            }else if(root.val < deleteNode.val) {
                root = root.right;
            }else {
                node = root;
                break;
            }
        }
        if (node == null) {
            return;
        }

        // 要删除的节点有两个子节点
        if (node.left != null && node.right != null) {
            // 找到节点的右子节点中的最小节点
            TreeNode pMinRight = node;
            TreeNode minRight = node.right;
            while (rightNode != null) {
                if (rightNode.left != null) {
                    pMinRight = rightNode;
                    rightNode = rightNode.left;
                }
                // 替换
                node.val = minRight.val;
                node = minRight;
                pNode = pMinRight;
            }
        }

        // 要删除的节点只有一个节点
        TreeNode child; // p的子节点
        if (node.left != null) {
            child = node.left;
        } else if (node.right != null) {
            child = node.right;
        } else {
            child = null;
        }

        // // 删除的是根节点
        if (pNode == null) {
            node = child;
        } else if (pNode.left == node) {
            pNode.left = child;
        } else {
            pNode.right = child;
        }
    }
}

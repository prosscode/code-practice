package code.algo.tree;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @Date 2022/2/19
 * @Author by shuang.peng
 * @Description BinaryTree 二叉树 -> 满二叉树，完全二叉树
 * 遍历方式：
 *  1。前序遍历
 *  2。中序遍历
 *  3。后序遍历
 *  4。层级遍历
 */
public class BinaryTree {

    // 前/中/后遍历，递归方式
    @Test
    public void preOrder(){
        TreeNode leftNode = new TreeNode(2,null,null);
        TreeNode rightNode = new TreeNode(3,null,null);
        TreeNode root = new TreeNode(1, leftNode, rightNode);
        getPreNode(root);
        HashMap<Object, Object> map = new HashMap<>();
    }

    void getPreNode(TreeNode node){
        if(node == null){
            return;
        }
        getPreNode(node.left);
        // 打印root节点
        System.out.println(node.val);
        getPreNode(node.right);
    }

    // 层序遍历，利用队列辅助
    @Test
    public void levelOrder(){
        TreeNode leftNode = new TreeNode(2,null,null);
        TreeNode rightNode = new TreeNode(3,null,null);
        TreeNode root = new TreeNode(1, leftNode, rightNode);
        LinkedList<TreeNode> nodes = new LinkedList<>();
        nodes.add(root);
        int n = 1;
        while (nodes.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(n + ":");
            int size = nodes.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = nodes.remove();
                buffer.append(node.val+ " ");
                if (node.left != null) {
                    nodes.add(node.left);
                }
                if (node.right != null) {
                    nodes.add(node.right);
                }
            }
            System.out.println(buffer);
            n ++;
        }
    }
}

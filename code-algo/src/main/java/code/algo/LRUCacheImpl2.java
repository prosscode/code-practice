package code.algo;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2022/3/27
 * @Author by shuang.peng
 * @Description 哈希表 + 双向链表实现LRU缓存
 * LRU缓存：Least Recently Used (LRU) cache，将最近最久未使用的元素予以淘汰。
 * 思路：
 * 1。用哈希表和双向链表维护所有在缓存中的键值对
 * 2。双向链表按照被使用的顺序存储键值对，靠近头部的键值对是最近使用的，而靠近尾部的键值对是最久未使用的
 * 3。哈希表即为普通的哈希映射（HashMap），通过缓存数据的键映射到其在双向链表中的位置
 * 首先使用哈希表进行定位，找出缓存项在双向链表中的位置，随后将其移动到双向链表的头部
 * 可在 O(1) 的时间内完成 get 或者 put 操作
 */
public class LRUCacheImpl2 {

    private int capacity;
    private int size;
    private DLinkedNode head, tail;
    private Map<Integer,DLinkedNode> cache;

    public static void main(String[] args) {
        LRUCacheImpl2 lRUCache = new LRUCacheImpl2(2);
        lRUCache.put(1, 1); // 缓存是 {1=1}
        lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
        lRUCache.get(1);    // 返回 1 {2=2, 1=1}
        lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存 {1=1, 3=3}
        lRUCache.get(2);    // 返回 -1 (未找到)
        lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存 {3=3, 4=4}
        lRUCache.get(1);    // 返回 -1 (未找到)
        lRUCache.get(3);    // 返回 3, 缓存 {4=4,3=3}
        lRUCache.get(4);    // 返回 4, 缓存 {3=3, 4=4}

        Map<Integer, DLinkedNode> cache = lRUCache.cache;
        cache.forEach((k,v)->{
            System.out.println(k+","+v.value);
        });
    }

    public LRUCacheImpl2(int capacity) {
        cache = new HashMap<>();
        // 初始化先伪造头尾两个节点
        head = new DLinkedNode();
        tail = new DLinkedNode();
        head.next=tail;
        tail.prev=head;
        this.size = 0;
        this.capacity = capacity;
    }

    // get操作
    int get(int key){
        DLinkedNode dLinkedNode = cache.get(key);
        if(dLinkedNode==null){
            return -1;
        }
        // 如果存在，则需要移除该节点
        dLinkedNode.prev.next=dLinkedNode.next;
        dLinkedNode.next.prev=dLinkedNode.prev;
        // 然后把该节点移动到双向链表的头部
        dLinkedNode.prev=head;
        dLinkedNode.next=head.next;
        head.next.prev=dLinkedNode;
        head.next=dLinkedNode;
        return dLinkedNode.value;
    }
    // put操作
    boolean put(int key,int value){
        DLinkedNode node = cache.get(key);
        if(node==null){
            // 如果不存在，创建新节点
            DLinkedNode newNode = new DLinkedNode(key, value);
            // 添加进哈希表
            cache.put(key, newNode);
            // 添加到双向链表的头部
            newNode.prev=head;
            newNode.next=head.next;
            head.next.prev=newNode;
            head.next=newNode;
            size++;
            if(size>capacity){
                // 如果超出容量，删除双向链表的尾部节点
                DLinkedNode tailNode = tail.prev;
                tailNode.prev.next = tailNode.next;
                tailNode.next.prev = tailNode.prev;
                // 从cache中移除
                cache.remove(tailNode.key);
                size--;
            }
        }else{
            // 如果存在，先修改value值
            node.value = value;
            // 然后移动到双向链表的头部
            node.prev=head;
            node.next=head.next;
            head.next.prev=node;
            head.next=node;
        }
        return true;
    }
    private void moveToHead(DLinkedNode node) {
        removeNode(node);
        addToHead(node);
    }
    private void addToHead(DLinkedNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    private void removeNode(DLinkedNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // 双向链表结构
    class DLinkedNode {
        int key;
        int value;
        DLinkedNode prev;
        DLinkedNode next;

        public DLinkedNode() {}

        public DLinkedNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

}

package code.algo;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Date 2022/3/27
 * @Author by shuang.peng
 * @Description LRU缓存：Least Recently Used (LRU) cache，将最近最久未使用的元素予以淘汰
 * 思路：直接用LinkedHashMap存储数据，既保证有序，又保证可以操作首尾元素
 */
public class LRUCacheImpl {
    // 哈希表
    private Map<Integer, Integer> cache;
    // 初始容量
    private int capacity;

    public static void main(String[] args) {
        LRUCacheImpl lRUCache = new LRUCacheImpl(2);
        lRUCache.put(1, 1); // 缓存是 {1=1}
        lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
        lRUCache.get(1);    // 返回 1 {2=2, 1=1}
        lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存 {1=1, 3=3}
        lRUCache.get(2);    // 返回 -1 (未找到)
        lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存 {3=3, 4=4}
        lRUCache.get(1);    // 返回 -1 (未找到)
        lRUCache.get(3);    // 返回 3, 缓存 {4=4,3=3}
        lRUCache.get(4);    // 返回 4, 缓存 {3=3, 4=4}
        Map<Integer, Integer> map = lRUCache.cache;
        map.forEach((k,v)->{
            System.out.println(k+","+v);
        });
    }

    public LRUCacheImpl(int capacity) {
        this.capacity = capacity;
        cache = new LinkedHashMap<>();
    }

    int get(int key){
        if(cache.containsKey(key)){
            int value = cache.get(key);
            cache.remove(key);
            // 保证每次查询后，都在末尾
            cache.put(key, value);
            return value;
        }
        // 不存在，返回-1
        return -1;
    }

    void put(int key, int value) {
        if (cache.containsKey(key)) {
            cache.remove(key);
        } else if (cache.size() == capacity) {
            // 如果容量满了，删除第一个元素
            Iterator<Map.Entry<Integer, Integer>> iterator = cache.entrySet().iterator();
            iterator.next();
            iterator.remove();

/*            Integer removeKey = cache.entrySet().iterator().next().getKey();
            cache.remove(removeKey);*/
        }

        // 加到末尾
        cache.put(key, value);
    }

}

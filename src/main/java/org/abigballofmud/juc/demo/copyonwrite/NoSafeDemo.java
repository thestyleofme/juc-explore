package org.abigballofmud.juc.demo.copyonwrite;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;
import org.apache.logging.log4j.spi.CopyOnWrite;

/**
 * <p>
 * 1. 异常
 * java.util.ConcurrentModificationException
 * 2. 导致原因
 * 多线程一边写一边读
 * 3. 解决方案
 * 3.1 Vector 线程安全
 * 3.2 Collections.synchronizedList(new ArrayList<>())
 * 3.3 CopyOnWriteArrayList
 * <p>
 * CopyOnWriteArrayList 写时复制，读写分离，看下CopyOnWriteArrayList.add(E e)源码即懂
 * 可以并发的读，而不需要加锁，因为当前容器不会添加任何元素，读写在不同容器
 * </p>
 *
 * @author isacc 2019/11/05 0:26
 * @since 1.0
 */
@Slf4j
public class NoSafeDemo {

    public static void main(String[] args) {
        // noSafeList();
        // noSafeSet();
        noSafeMap();
    }

    private static void noSafeMap() {
        // Map<String, String> map = new HashMap<>(64);
        // Map<String, String> map = Collections.synchronizedMap(new HashMap<>(64));
        Map<String, String> map = new ConcurrentHashMap<>(64);
        int times = 40;
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 8));
                log.debug("map: {}", map);
            });
        }
        executorService.shutdown();
    }

    private static void noSafeSet() {
        // Set<String> set = new HashSet<>();
        // Set<String> set = Collections.synchronizedSet(new HashSet<>());
        Set<String> set = new CopyOnWriteArraySet<>();
        int times = 30;
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                set.add(UUID.randomUUID().toString().substring(0, 8));
                log.debug("set: {}", set);
            });
        }
        executorService.shutdown();
    }

    private static void noSafeList() {
        // List<String> list = new ArrayList<>();
        // List<String> list = new Vector<>();
        // List<String> list = Collections.synchronizedList(new ArrayList<>());
        List<String> list = new CopyOnWriteArrayList<>();
        int times = 30;
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 8));
                log.debug("list: {}", list);
            });
        }
        executorService.shutdown();
    }
}

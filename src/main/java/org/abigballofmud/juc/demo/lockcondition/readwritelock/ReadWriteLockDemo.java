package org.abigballofmud.juc.demo.lockcondition.readwritelock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;

/**
 * <p>
 * 多个线程同时读取一个资源类没有任何问题，所以为了满足并发量，读取共享资源应该可以同时进行
 * 但是，如果有一个线程想去写共享资源，就不应该再有其他线程可以对该资源进行读或写
 * 总结：
 * 读-读 能共存
 * 读-写 不能共存
 * 写-写 不能共存
 * </p>
 *
 * @author isacc 2019/11/18 1:20
 * @since 1.0
 */
@Slf4j
public class ReadWriteLockDemo {

    public static void main(String[] args) {
        int times = 5;
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        MyCache myCache = new MyCache();
        for (int i = 0; i < times; i++) {
            final int temp = i;
            executorService.execute(() -> myCache.put(String.valueOf(temp), temp));
        }
        for (int i = 0; i < times; i++) {
            final int temp = i;
            executorService.execute(() -> myCache.get(String.valueOf(temp)));
        }
        executorService.shutdown();
    }

}

@Slf4j
@SuppressWarnings("WeakerAccess")
class MyCache {

    private volatile Map<String, Object> map = new HashMap<>(16);
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void put(String key, Object value) {
        readWriteLock.writeLock().lock();
        try {
            log.debug("开始写数据{}", key);
            map.put(key, value);
            TimeUnit.MILLISECONDS.sleep(300);
            log.debug("数据{}写入完成", key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public Object get(String key) {
        readWriteLock.readLock().lock();
        try {
            log.debug("开始读数据{}", key);
            Object value = map.get(key);
            log.debug("数据{}读取完成", key);
            return value;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
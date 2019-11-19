package org.abigballofmud.juc.demo.threadpool;

import java.util.concurrent.*;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;

/**
 * <p>
 * 生产中都手动创建线程
 * 线程池不允许使用Executors去创建，而是通过ThreadPoolExecutor的方式，这样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。
 * 说明：Executors各个方法的弊端：
 * * 1）newFixedThreadPool和newSingleThreadExecutor:
 * *   主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至OOM。
 * * 2）newCachedThreadPool和newScheduledThreadPool:
 * *   主要问题是线程数最大数是Integer.MAX_VALUE，可能会创建数量非常多的线程，甚至OOM。
 *
 * @author isacc 2019/11/19 23:58
 * @see ThreadPoolUtil
 * </p>
 * @since 1.0
 */
@Slf4j
public class MyThreadPoolDemo {

    public static void main(String[] args) {
        // 线程池中固定5个线程
        // ExecutorService executorService = Executors.newFixedThreadPool(5);
        // 线程池中只有1个线程
        // ExecutorService executorService = Executors.newSingleThreadExecutor();
        // 可扩容，N个线程
        // ExecutorService executorService = Executors.newCachedThreadPool();
        // 查看系统CPU核数
        log.debug("当前系统CPU核数：{}", Runtime.getRuntime().availableProcessors());
        // CPU密集型 一般线程大小设置为 核数+1
        // IO密集型  CPU核数*(1+平均等待时间/平均工作时间)
        ExecutorService executorService = new ThreadPoolExecutor(2,
                5,
                2,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),
                Executors.defaultThreadFactory(),
                // 直接跑异常 终止
                // new ThreadPoolExecutor.AbortPolicy()
                // 回退 调用方去执行 这里相当于main去执行
                // new ThreadPoolExecutor.CallerRunsPolicy()
                // 直接抛弃，不予处理不抛异常
                // new ThreadPoolExecutor.DiscardPolicy()
                // 抛弃队列中等待最久的任务，然后把当前任务放入队列
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
        int times = 10;
        try {
            for (int i = 0; i < times; i++) {
                // Executors.newCachedThreadPool(); 模仿 可复用线程。不用一直创
                // try {
                //     TimeUnit.SECONDS.sleep(1);
                // } catch (InterruptedException e) {
                //     Thread.currentThread().interrupt();
                // }
                executorService.execute(() -> log.debug("{} \t execute", Thread.currentThread().getName()));
            }
        } finally {
            executorService.shutdown();
        }
    }
}

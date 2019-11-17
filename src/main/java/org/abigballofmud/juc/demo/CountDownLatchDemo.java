package org.abigballofmud.juc.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;

/**
 * <p>
 * CountDownLatch主要有两个方法，当一个或多个线程调用await方法时，这些线程会阻塞
 * 其他线程调用countDown方法时会将计数器减1（调用countDown方法的线程不会阻塞）
 * 当计数器的值变为0时，因await方法阻塞的线程会被唤醒，继续执行
 * </p>
 *
 * @author isacc 2019/11/17 23:56
 * @since 1.0
 */
@Slf4j
public class CountDownLatchDemo {

    public static void main(String[] args) {
        int times = 5;
        CountDownLatch countDownLatch = new CountDownLatch(times);
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                log.debug("{}下班走人", Thread.currentThread().getName());
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.debug("人走完了，关门");
        executorService.shutdown();
    }
}

package org.abigballofmud.juc.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;

/**
 * <p>
 * 在信号量定义了两种操作：
 * acquire，当一个线程调用acquire时，它要么成功获取信号量（信号量减1），要么一直等下去，直到有线程释放信号量或超时
 * release，实际上会将信号量的值加1，然后唤醒等待的线程
 * <p>
 * 信号量主要有两个目的，一个用于多个共享资源的互斥使用，另一个用于并发线程数的控制
 * </p>
 * </p>
 *
 * @author isacc 2019/11/18 0:45
 * @since 1.0
 */
@Slf4j
public class SemaphoreDemo {

    public static void main(String[] args) {
        // 这里模仿6辆车抢3个车位
        int parkingSpace = 3;
        int personNumber = 6;
        Semaphore semaphore = new Semaphore(parkingSpace);
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        for (int i = 0; i < personNumber; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    log.debug("{}抢到了车位", Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(3);
                    log.debug("{}离开了车位", Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();
                }
            });
        }
        executorService.shutdown();
    }
}

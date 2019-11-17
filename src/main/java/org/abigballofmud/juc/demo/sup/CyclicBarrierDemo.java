package org.abigballofmud.juc.demo.sup;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;

/**
 * <p>
 * CyclicBarrier跟CountDownLatch相反，做加1
 * 等多个线程执行完了后执行
 * </p>
 *
 * @author isacc 2019/11/18 0:19
 * @since 1.0
 */
@Slf4j
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        int times = 7;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(times, () -> log.debug("七龙珠已集齐，开始召唤神龙"));
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        for (int i = 0; i < times; i++) {
            final int temp = i;
            executorService.execute(() -> {
                log.debug("收集第{}颗龙珠", temp + 1);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        executorService.shutdown();
    }
}

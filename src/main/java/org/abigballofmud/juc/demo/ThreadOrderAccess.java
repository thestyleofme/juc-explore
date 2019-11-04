package org.abigballofmud.juc.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;

/**
 * <p>
 * 多线程之间按顺序调用，实现 A -> B -> C
 * 三个线程启动，要求如下：
 * A打印1次，B打印2次，C打印3次
 * 接着
 * A打印1次，B打印2次，C打印3次
 * ......来10轮
 * 4. <em>标志位</em>
 * </p>
 *
 * @author isacc 2019/11/03 3:07
 * @since 1.0
 */
public class ThreadOrderAccess {

    public static void main(String[] args) {
        SharedResource sharedResource = SharedResource.builder().number(1).build();
        int times = 10;
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        executorService.execute(() -> {
            try {
                for (int i = 0; i < times; i++) {
                    sharedResource.aDo();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executorService.execute(() -> {
            try {
                for (int i = 0; i < times; i++) {
                    sharedResource.bDo();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executorService.execute(() -> {
            try {
                for (int i = 0; i < times; i++) {
                    sharedResource.cDo();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executorService.shutdown();
    }
}

@SuppressWarnings("WeakerAccess")
@Slf4j
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
class SharedResource {

    private int number;
    private static final int A_FLAG = 1;
    private static final int B_FLAG = 2;
    private static final int C_FLAG = 3;
    private final Lock lock = new ReentrantLock();
    private final Condition conditionA = lock.newCondition();
    private final Condition conditionB = lock.newCondition();
    private final Condition conditionC = lock.newCondition();

    private void printNumber(int times) {
        for (int i = 0; i < times; i++) {
            log.debug(String.format("%s\t%d", Thread.currentThread().getName(), i));
        }
    }

    public void aDo() throws InterruptedException {
        lock.lock();
        try {
            // 判断 不是A干 A wait
            while (number != A_FLAG) {
                conditionA.await();
            }
            // 处理
            printNumber(1);
            // 通知
            conditionB.signal();
            // 标志位
            number = B_FLAG;
        } finally {
            lock.unlock();
        }
    }

    public void bDo() throws InterruptedException {
        lock.lock();
        try {
            // 判断 不是A干 A wait
            while (number != B_FLAG) {
                conditionB.await();
            }
            // 处理
            printNumber(2);
            // 通知
            conditionC.signal();
            // 标志位
            number = C_FLAG;
        } finally {
            lock.unlock();
        }
    }

    public void cDo() throws InterruptedException {
        lock.lock();
        try {
            // 判断 不是C干 C wait
            while (number != C_FLAG) {
                conditionC.await();
            }
            // 处理
            printNumber(3);
            // 通知
            conditionA.signal();
            // 标志位
            number = A_FLAG;
        } finally {
            lock.unlock();
        }
    }

}
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
 * 生成者消费者模式
 * 多个线程交替运行 多个生产 多个消费
 * 生产一个消费一个
 * 2. <em>判断</em> <em>处理</em> <em>通知</em>
 * 3. <em>多线程交互中，必须防止多线程的虚假唤醒，也即(判断只用while，不用if)</em>
 * </p>
 *
 * @author isacc 2019/11/03 1:14
 * @since 1.0
 */
@SuppressWarnings("DuplicatedCode")
public class ThreadWaitNotifyDemo {

    public static void main(String[] args) {
        Food food = Food.builder().number(0).build();
        int times = 10;
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        executorService.execute(() -> {
            try {
                for (int i = 0; i < times; i++) {
                    food.produce();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executorService.execute(() -> {
            try {
                for (int i = 0; i < times; i++) {
                    food.consumer();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executorService.execute(() -> {
            try {
                for (int i = 0; i < times; i++) {
                    food.produce();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executorService.execute(() -> {
            try {
                for (int i = 0; i < times; i++) {
                    food.consumer();
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
class Food {

    private int number;
    final Lock lock = new ReentrantLock();
    final Condition condition = lock.newCondition();

    public void produce() throws InterruptedException {
        lock.lock();
        try {
            while (number != 0) {
                condition.await();
            }
            // 处理逻辑
            number++;
            log.debug(String.format("%s\t生产了，number: %d", Thread.currentThread().getName(), number));
            // 通知
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void consumer() throws InterruptedException {
        lock.lock();
        try {
            // 判断 没有了food就等着生产出来
            while (number == 0) {
                condition.await();
            }
            // 处理逻辑
            number--;
            log.debug(String.format("%s\t消费了，number: %d", Thread.currentThread().getName(), number));
            // 通知
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /*public synchronized void produce() throws InterruptedException {
        // 判断 有food就等着被消费 这里条件是保证生产一个 消费一个
        // 注意 多线程交互判断都使用while进行判断 使用if会出错
        // 因为if只判断一次 当线程被唤醒时 不重新判断 使用while可以重新判断
        while (number != 0) {
            this.wait();
        }
        // 处理逻辑
        number++;
        log.debug(String.format("%s\t生产了，number: %d", Thread.currentThread().getName(), number));
        // 通知
        this.notifyAll();
    }

    public synchronized void consumer() throws InterruptedException {
        // 判断 没有了food就等着生产出来
        while (number == 0) {
            this.wait();
        }
        // 处理逻辑
        number--;
        log.debug(String.format("%s\t消费了，number: %d", Thread.currentThread().getName(), number));
        // 通知
        this.notifyAll();
    }*/

}


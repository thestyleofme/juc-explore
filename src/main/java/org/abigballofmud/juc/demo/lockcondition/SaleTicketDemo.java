package org.abigballofmud.juc.demo.lockcondition;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * <p>
 * 示例：几个售票员 卖出多少张票
 * 1. 在高内聚低耦合的前提下，<em>线程</em> <em>操作(对外暴露的调用方法)</em> <em>资源类</em>
 * </p>
 *
 * @author isacc 2019/11/02 0:59
 * @since 1.0
 */
@Slf4j
public class SaleTicketDemo {

    public static void main(String[] args) {
        final Ticket ticket = Ticket.builder().number(10).build();
        int saleNumber = 20;
        // Thread.start()后变为就绪状态，等待操作系统分配CPU去运行
        // 线程的几种状态 Thread.State
        // ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        ThreadPoolTaskExecutor asyncExecutor = ThreadPoolUtil.getAsyncExecutor();
        for (int i = 0; i < saleNumber; i++) {
            // executorService.execute(ticket::saleTicket);
            asyncExecutor.execute(ticket::saleTicket);
        }
        // 由于ExecutorService不关闭 main线程不退出，这里关闭下
        // executorService.shutdown();
        asyncExecutor.shutdown();
    }
}

@SuppressWarnings("WeakerAccess")
@Slf4j
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
class Ticket {

    private final Lock lock = new ReentrantLock();

    private Integer number;

    public void saleTicket() {
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            if (number > 0) {
                log.debug(String.format("%s\t卖出第%d张票，还剩%d张票", Thread.currentThread().getName(), number--, number));
            } else {
                log.debug(String.format("%s\t票已卖完！！！", Thread.currentThread().getName()));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}
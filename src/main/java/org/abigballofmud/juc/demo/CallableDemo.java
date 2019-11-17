package org.abigballofmud.juc.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;
import org.abigballofmud.juc.utils.ThreadPoolUtil;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2019/11/06 1:23
 * @since 1.0
 */
@Slf4j
public class CallableDemo {

    public static void main(String[] args) {
        // FutureTask<Integer> futureTask = new FutureTask<>(new MyThread());
        // // futureTask 实现了 runnable以及Callable 这里利用多态 futureTask只会执行一次
        // new Thread(futureTask, "demo-callable").start();
        // try {
        //     log.debug("value: {}", futureTask.get());
        // } catch (InterruptedException | ExecutionException e) {
        //     log.error("value get error", e);
        // }
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        int times = 30;
        List<Future<Integer>> futureTaskList = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            futureTaskList.add(executorService.submit(new MyThread()));
        }
        int result = futureTaskList.stream().mapToInt(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("value get error", e);
            }
            return 0;
        }).sum();
        log.debug("sum: {}", result);
        executorService.shutdown();
    }
}

/**
 * 这里的泛型就是返回值类型
 */
@Slf4j
class MyThread implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        log.debug(Thread.currentThread().getName() + "come here~");
        return 1;
    }
}
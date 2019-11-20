package org.abigballofmud.juc.demo.threadpool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2019/11/21 2:07
 * @since 1.0
 */
@Slf4j
public class CompletableFutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 同步调用
        CompletableFuture completableFuture = CompletableFuture.runAsync(() ->
                log.debug("{}, 无返回值", Thread.currentThread().getName())
        );
        completableFuture.get();

        // 异步调用
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            log.debug("{}, 有返回值", Thread.currentThread().getName());
            // int errorCompute = 10 / 0;
            return 1024;
        });
        log.debug("result: {}",
                integerCompletableFuture.whenComplete((t, u) -> {
                    log.debug("t: {}", t);
                    log.error("u: {}", u.getMessage());
                }).exceptionally(e -> {
                    log.error("error: {}", e.getMessage());
                    return 4444;
                }).get());

    }
}

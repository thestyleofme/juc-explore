package org.abigballofmud.juc.demo.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 详情见org/abigballofmud/juc/demo/threadpool/BlockQueue核心方法.png
 * </p>
 *
 * @author isacc 2019/11/19 0:29
 * @since 1.0
 */
public class BlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(3);
        // 抛异常
        System.out.println(arrayBlockingQueue.add("a"));
        System.out.println(arrayBlockingQueue.remove());
        System.out.println(arrayBlockingQueue.element());
        // 返回true false null
        System.out.println(arrayBlockingQueue.offer("b"));
        System.out.println(arrayBlockingQueue.offer("b"));
        System.out.println(arrayBlockingQueue.offer("b"));
        System.out.println(arrayBlockingQueue.offer("b", 1, TimeUnit.SECONDS));
        System.out.println(arrayBlockingQueue.poll());
        System.out.println(arrayBlockingQueue.poll(1, TimeUnit.SECONDS));
        System.out.println(arrayBlockingQueue.peek());
        // 阻塞
        arrayBlockingQueue.put("c");
        System.out.println(arrayBlockingQueue.take());

    }


}

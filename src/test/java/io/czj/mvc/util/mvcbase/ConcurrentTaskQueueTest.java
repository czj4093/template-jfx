package io.czj.mvc.util.mvcbase;

import io.czj.mvc.util.ConcurrentTaskQueue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
class ConcurrentTaskQueueTest {

    @Test
    void testSequenceGuarantees() throws InterruptedException {
        // 给定
        final ConcurrentTaskQueue<Integer> taskQueue = new ConcurrentTaskQueue<>();
        final ConcurrentLinkedQueue<Integer> collector = new ConcurrentLinkedQueue<>();
        // 当我们同时生产和消费一些数字时
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            taskQueue.submit(
                    () -> {
                        try {
                            // 强制一些线程切换以使测试更真实
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return finalI;
                    },
                    collector::add
            );
        }
        // 测试用例中的特殊情况：等待所有并发任务完成
        // 这样我们就可以同步断言结果。
        // 总体思路是向 CTQ 提交最后一个任务，同时设置一个我们可以同步等待。
        CountDownLatch latch = new CountDownLatch(1);
        taskQueue.submit(() -> {
            latch.countDown();
            return null;
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        // 那么没有数字丢失并且序列被保留
        Integer[] expected = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertArrayEquals(expected, collector.toArray());
    }

}
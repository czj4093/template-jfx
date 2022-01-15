package io.czj.mvc.util;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 可以提交任务以供执行的设备。执行是异步的 - 可能在不同的线程中 - 但序列保持稳定, 这样对于所有任务A和B：如果B在A之后提交，B只会在A完成后执行。
 * <p>
 * 新任务可以在任务运行时提交。任务提交本身应该是线程受限的，即 ConcurrentTaskQueue 的创建和任务提交预计在同一个线程中运行，很可能是 JavaFX UI 应用程序线程。
 *
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public final class ConcurrentTaskQueue<R> {

    private final ExecutorService executor;
    private final ConcurrentLinkedQueue<Task<R>> buffer;
    private final Duration maxToDoTime;

    // 对于非线程限制的提交，我们可能需要一个 AtomicBoolean
    private boolean running = false;

    public ConcurrentTaskQueue() {
        this(Duration.ofSeconds(5));
    }

    public ConcurrentTaskQueue(Duration maxToDoTime) {
        this.maxToDoTime = maxToDoTime;
        // 使用 2 将 onDone 与下一个待办事项重叠
        this.executor = Executors.newFixedThreadPool(1);
        this.buffer = new ConcurrentLinkedQueue<>();
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void submit(Supplier<R> todo) {
        submit(todo, r -> {});
    }

    public void submit(Supplier<R> todo, Consumer<R> onDone) {
        buffer.add(new Task<>(todo, onDone));
        execute();
    }

    private void execute() {
        if (running) {
            return;
        }

        final Task<R> task = buffer.poll();

        if (task == null) {
            return;
        }

        running = true;

        final Future<R> todoFuture = executor.submit(task.todo::get);

        Runnable onDoneRunnable = () -> {
            try {
                final R r = todoFuture.get(maxToDoTime.getSeconds(), TimeUnit.SECONDS);
                task.onDone.accept(r);
            } catch (Exception e) {
                e.printStackTrace();
                // todo: 考虑更好的异常处理
            } finally {
                running = false;
                execute();
            }
        };
        executor.submit(onDoneRunnable);
    }

    private static class Task<T> {

        // to-do 的返回类型 ..
        private final Supplier<T> todo;

        // .. 必须匹配 onDone 的输入类型
        private final Consumer<T> onDone;

        public Task(Supplier<T> todo, Consumer<T> onDone) {
            this.todo = todo;
            this.onDone = onDone;
        }
    }

}

package io.czj.mvc.util;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 所有控制器的基类。整个应用程序逻辑位于控制器类中。控制器类处理和管理模型。模型封装了整个应用程序状态。
 * <p>
 * 控制器提供应用程序的全部核心功能，即所谓的“动作”,动作的执行是异步的。序列保持稳定，使得
 * <p>
 * 对于A和B所有动作：如果B在A之后提交，B只会在A完成后执行。
 *
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public abstract class ControllerBase<M> {

    private ConcurrentTaskQueue<M> actionQueue;

    // 此 Controller 管理的模型。只有子类可以直接访问
    protected final M model;

    /**
     * 控制器需要一个模型。
     *
     * @param model 此 Controller 管理的模型
     */
    protected ControllerBase(M model) {
        Objects.requireNonNull(model);

        this.model = model;
    }

    public void shutdown() {
        if (null != actionQueue) {
            actionQueue.shutdown();
            actionQueue = null;
        }
    }

    /**
     * 在外部线程中以严格的顺序异步调度给定的操作以执行。操作完成后立即调用 onDone
     */
    protected void async(Supplier<M> action, Consumer<M> onDone) {
        if (null == actionQueue) {
            actionQueue = new ConcurrentTaskQueue<>();
        }
        actionQueue.submit(action, onDone);
    }


    /**
     * 在外部线程中以严格的顺序异步调度给定的操作以执行。
     */
    protected void async(Runnable todo) {
        async(() -> {
                    todo.run();
                    return model;
                },
                m -> {
                });
    }

    /**
     * 在已安排的所有操作完成后安排给定的操作。
     */
    public void runLater(Consumer<M> action) {
        async(() -> model, action);
    }

    /**
     * TestCase 支持的中间解决方案。
     * <p>
     * 最好的解决方案是在调用线程上执行runLater的动作。
     * <p>
     * 等待直到 actionQueue 中的所有当前操作都完成。
     * <p>
     * 在大多数情况下，从应用程序中调用此方法是错误的。
     */
    public void awaitCompletion() {
        if (actionQueue == null) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        actionQueue.submit(() -> {
            latch.countDown();
            return null;
        });
        try {
            //noinspection ResultOfMethodCallIgnored
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException("CountDownLatch was interrupted");
        }
    }

    /**
     * 只有其他基类ViewMixin和PUI_Base需要访问，因此它是包私有
     */
    M getModel() {
        return model;
    }

    /**
     * 即使设置一个值，控制器也有责任。
     * <p>
     * 没有应用程序特定的类可以访问 ObservableValue.setValue
     * <p>
     * 值是异步设置的。
     */
    protected <V> void setValue(ObservableValue<V> observableValue, V newValue) {
        async(() -> observableValue.setValue(newValue));
    }

    protected <V> V get(ObservableValue<V> observableValue) {
        return observableValue.getValue();
    }

    /**
     * 切换 ObservableValue<Boolean> 的便捷方法
     */
    protected void toggle(ObservableValue<Boolean> observableValue) {
        async(() -> observableValue.setValue(!observableValue.getValue()));
    }

    /**
     * 将 ObservableValue<Integer> 增加 1 的便捷方法
     */
    protected void increase(ObservableValue<Integer> observableValue) {
        async(() -> observableValue.setValue(observableValue.getValue() + 1));
    }

    /**
     * 将 ObservableValue<Integer> 减 1 的便捷方法
     */
    protected void decrease(ObservableValue<Integer> observableValue) {
        async(() -> observableValue.setValue(observableValue.getValue() - 1));
    }

    /**
     * 用于在指定时间量内暂停执行操作的实用程序功能。
     * <p>
     * An {@link InterruptedException} 再次设置中断标志时将被捕获并忽略。
     *
     * @param duration time to sleep
     */
    protected void pauseExecution(Duration duration) {
        async(() -> {
            try {
                Thread.sleep(duration.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * 如果您需要在一个异步调用中更新多个 ObservableValues，请使用此选项。
     * <p>
     * 使用 'set' 获取合适的 Setter
     */
    protected void updateModel(Setter<?>... setters) {
        async(() -> {
            for (Setter<?> setter : setters) {
                setter.setValue();
            }
        });
    }

    protected <V> Setter<V> set(ObservableValue<V> observableValue, V value) {
        return new Setter<V>(observableValue, value);
    }

    protected static class Setter<V> {
        private final ObservableValue<V> observableValue;
        private final V value;

        private Setter(ObservableValue<V> observableValue, V value) {
            this.observableValue = observableValue;
            this.value = value;
        }

        void setValue() {
            observableValue.setValue(value);
        }
    }

}

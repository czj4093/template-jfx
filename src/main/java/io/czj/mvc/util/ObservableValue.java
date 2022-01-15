package io.czj.mvc.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Observable-Pattern 的基本实现。
 *
 * 准备好根据您的要求增强此功能。
 *
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public final class ObservableValue<V> {

    // 每当值更改时，所有这些侦听器都会收到通知
    private final Set<ValueChangeListener<V>> listeners = new HashSet<>();

    private volatile V value;

    public ObservableValue(V initialValue) {
        value = initialValue;
    }

    /**
     * 注册一个新的观察者（又名listener）
     *
     * @param listener 指定值更改时需要执行的操作
     */
    public void onChange(ValueChangeListener<V> listener) {
        listeners.add(listener);
        // 立即通知侦听器
        listener.update(value, value);
    }

    /**
     * 这是ObservableValue的核心功能。每次值更改时，都会通知所有侦听器。
     *
     * 此方法是包私有，仅允许ControllerBase设置新值。
     *
     * 对于 UI，setValue 不可访问
     *
     * @param newValue 新值
     */
    void setValue(V newValue) {
        // 如果值未更改，则不通知
        if (Objects.equals(value, newValue)) {
            return;
        }
        V oldValue = value;
        value = newValue;

        listeners.forEach(listener -> {
            // 预先订购的侦听器可能已经改变了这一点，因此回调不再适用
            if (value.equals(newValue)) {
                listener.update(oldValue, newValue);
            }
        });
    }

    /**
     * 公开这个没问题。
     *
     * @return 此 ObservableValue 管理的值
     */
    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @FunctionalInterface
    public interface ValueChangeListener<V> {
        void update(V oldValue, V newValue);
    }

}

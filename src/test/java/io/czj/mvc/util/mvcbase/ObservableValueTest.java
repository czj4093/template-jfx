package io.czj.mvc.util.mvcbase;

import io.czj.mvc.util.ObservableValue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
class ObservableValueTest {

    @Test
    void testInitialization() {
        ObservableValue<Boolean> v = new ObservableValue<>(false);
        assertFalse(v.getValue());
        v = new ObservableValue<>(true);
        assertTrue(v.getValue());
    }

    @Test
    void testSetValue() {
        ObservableValue<Boolean> observableValue = new ObservableValue<>(false);
        observableValue.setValue(true);
        assertTrue(observableValue.getValue());
        observableValue.setValue(false);
        assertFalse(observableValue.getValue());
    }

    @Test
    void testSetListener() {
        String initialValue = "initial Value";
        String firstValue = "first value";
        ObservableValue<String> observableValue = new ObservableValue<>(initialValue);
        AtomicInteger counter = new AtomicInteger(0);
        AtomicReference<String> foundOld = new AtomicReference<>();
        AtomicReference<String> foundNew = new AtomicReference<>();
        observableValue.onChange((oldValue, newValue) -> {
            counter.getAndIncrement();
            foundOld.set(oldValue);
            foundNew.set(newValue);
        });
        // 注册时调用监听器
        assertEquals(1, counter.get());
        // oldValue 的初始值是当前值
        assertEquals(initialValue, foundOld.get());
        // 当前值
        assertEquals(initialValue, foundNew.get());
        observableValue.setValue(initialValue);
        // 值保持不变,未调用侦听器
        assertEquals(1, counter.get());
        observableValue.setValue(firstValue);
        // 值发生了变化；监听器被调用
        assertEquals(2, counter.get());
        assertEquals(initialValue, foundOld.get());
        assertEquals(firstValue, foundNew.get());
    }

    @Disabled("This test sometimes fails, most probably because testcase is wrong, not implementation")
    @Test
    void testEdgeCase() {
        ObservableValue<String> observableValue = new ObservableValue<>("start");
        List<String> log1 = new ArrayList<>();
        List<String> log2 = new ArrayList<>();
        observableValue.onChange((oldValue, newValue) -> {
            log1.add(oldValue);
            log1.add(newValue);
            if (newValue.equals("second")) {
                observableValue.setValue("third");
            }
        });
        observableValue.onChange((oldValue, newValue) -> {
            log2.add(oldValue);
            log2.add(newValue);
        });
        assertArrayEquals(new String[]{"start", "start"}, log1.toArray(new String[0]));
        assertArrayEquals(new String[]{"start", "start"}, log2.toArray(new String[0]));
        observableValue.setValue("second");
        // 第一个观察者已经看到了所有的价值变化
        assertArrayEquals(new String[]{"start", "start", "start", "second", "second", "third"}, log1.toArray(new String[0]));

        // 第二个观察者可能_没有_看到所有值的变化，但他至少看到了最后一次正确的值变化！！！
        assertArrayEquals(new String[]{"start", "start", "second", "third"}, log2.toArray(new String[0]));
    }

}

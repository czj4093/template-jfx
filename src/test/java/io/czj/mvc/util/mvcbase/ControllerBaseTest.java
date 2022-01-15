package io.czj.mvc.util.mvcbase;

import io.czj.mvc.util.ControllerBase;
import io.czj.mvc.util.ObservableValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public class ControllerBaseTest {

    private TestModel model;
    private ControllerBase<TestModel> controller;

    @BeforeEach
    void setup() {
        model = new TestModel();
        controller = new ControllerBase<>(model) {
        };
    }

    @Test
    void testInitialization() {
        assertSame(model, controller.getModel());
    }

    @Test
    void testSetValue() {
        int newInt = 42;
        boolean newBool = true;
        controller.setValue(model.someInt, newInt);
        controller.setValue(model.someBoolean, newBool);
        controller.awaitCompletion();
        assertEquals(newInt, model.someInt.getValue());
        assertEquals(newBool, model.someBoolean.getValue());
    }

    @Test
    void testToggle() {
        model.someBoolean.setValue(true);
        controller.toggle(model.someBoolean);
        controller.awaitCompletion();
        assertFalse(model.someBoolean.getValue());
        controller.toggle(model.someBoolean);
        controller.awaitCompletion();
        assertTrue(model.someBoolean.getValue());
    }

    private static class TestModel {
        final ObservableValue<Integer> someInt = new ObservableValue<>(73);
        final ObservableValue<Boolean> someBoolean = new ObservableValue<>(false);
    }

}

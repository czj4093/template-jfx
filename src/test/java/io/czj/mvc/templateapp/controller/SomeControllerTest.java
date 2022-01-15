package io.czj.mvc.templateapp.controller;

import io.czj.mvc.templateapp.model.SomeModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
class SomeControllerTest {

    @Test
    void testCounter() {
        SomeModel model = new SomeModel();
        int initialCount = model.counter.getValue();
        SomeController controller = new SomeController(model);
        controller.increaseCounter();
        controller.awaitCompletion();
        assertEquals(initialCount + 1, model.counter.getValue());
        controller.decreaseCounter();
        controller.awaitCompletion();
        assertEquals(initialCount, model.counter.getValue());
    }

    @Test
    void testLED() {
        SomeModel model = new SomeModel();
        SomeController controller = new SomeController(model);
        controller.setLedGlows(true);
        controller.awaitCompletion();
        assertTrue(model.ledGlows.getValue());
        controller.setLedGlows(false);
        controller.awaitCompletion();
        assertFalse(model.ledGlows.getValue());
    }

}
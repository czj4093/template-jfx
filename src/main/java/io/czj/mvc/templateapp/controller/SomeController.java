package io.czj.mvc.templateapp.controller;

import io.czj.mvc.templateapp.model.SomeModel;
import io.czj.mvc.util.ControllerBase;

/**
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public class SomeController extends ControllerBase<SomeModel> {

    public SomeController(SomeModel model) {
        super(model);
    }

    // 我们在应用程序中需要的逻辑
    // 这些方法可以从 GUI（以及其他任何地方）调用

    public void increaseCounter() {
        increase(model.counter);
    }

    public void decreaseCounter() {
        decrease(model.counter);
    }

    public void setLedGlows(boolean glows){
        setValue(model.ledGlows, glows);
    }

}

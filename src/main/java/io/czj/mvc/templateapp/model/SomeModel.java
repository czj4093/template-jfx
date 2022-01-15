package io.czj.mvc.templateapp.model;

import io.czj.mvc.util.ObservableValue;

/**
 * 在 MVC 中，Model主要由ObservableValues组成。
 *
 * 应该不需要额外的方法。
 *
 * 所有应用程序逻辑都由Controller处理
 *
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public class SomeModel {

    public final ObservableValue<String>  systemInfo = new ObservableValue<>("JavaFX " + System.getProperty("javafx.version") + ", running on Java " + System.getProperty("java.version") + ".");
    public final ObservableValue<Integer> counter    = new ObservableValue<>(73);
    public final ObservableValue<Boolean> ledGlows   = new ObservableValue<>(false);

}

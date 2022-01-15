package io.czj.mvc.util;

import io.czj.mvc.util.ControllerBase;
import io.czj.mvc.util.ObservableValue;
import io.czj.mvc.util.Projector;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.scene.text.Font;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 将此接口用于所有 GUI 部分以确保实现的一致性。它提供了使 MVC 运行的基本功能。
 *
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public interface ViewMixin<M, C extends ControllerBase<M>> extends Projector<M, C> {

    @Override
    default void init(C controller) {
        Projector.super.init(controller);
        layoutParts();
    }

    /**
     * the method name says it all
     */
    void layoutParts();

    /**
     * 只是加载样式表文件的便捷方法
     *
     * @param stylesheetFiles name of the stylesheet file
     */
    default void addStylesheetFiles(String... stylesheetFiles) {
        for (String file : stylesheetFiles) {
            String stylesheet = getClass().getResource(file).toExternalForm();
            getStylesheets().add(stylesheet);
        }
    }

    /**
     * 只是一种方便的方法来加载额外的字体
     */
    default void loadFonts(String... fonts) {
        for (String f : fonts) {
            Font.loadFont(getClass().getResourceAsStream(f), 0);
        }
    }

    List<String> getStylesheets();

    /**
     * 注册观察者的起点。
     *
     * @param observableValue 需要观察的值
     * @return 一个 'Converter' 指定一个函数将 'ObservableValue' 的类型转换为 'Property' 的类型
     */
    default <V> Converter<V> onChangeOf(ObservableValue<V> observableValue) {
        return new Converter<>(observableValue);
    }

    /**
     *
     */
    class Converter<V> {

        private ObservableValue<V> observableValue;

        public Converter(ObservableValue<V> observableValue) {
            this.observableValue = observableValue;
        }

        /**
         * 注册观察者以指定转换器功能的第二步（可选）
         *
         * @param converter 将ObservableValue类型转换为Property类型的函数
         * @return 一个更新程序，用于指定如果ObservableValue发生更改则需要更新的GUI-Property
         */
        public <R> Updater<V, R> convertedBy(Function<V, R> converter) {
            return new Updater<>(observableValue, converter);
        }

        /**
         * 注册一个没有任何类型转换的观察者，这将使属性值和 observableValue 保持同步。
         *
         * @param property 当 observableValue 改变时将更新的 GUI-Property
         */
        public void update(Property<? super V> property) {
            execute((oldValue, newValue) -> property.setValue(newValue));
        }

        /**
         * 注册一个观察者。
         *
         * @param listener 当 observableValue 改变时需要在 GUI 上做的任何事情
         */
        public void execute(ObservableValue.ValueChangeListener<V> listener) {
            observableValue.onChange((oldValue, newValue) -> Platform.runLater(() -> listener.update(oldValue, newValue)));
        }
    }

    class Updater<V, P> {

        private ObservableValue<V> observableValue;

        // 将ObservableValue类型转换为Property类型的函数
        private Function<V, P> converter;

        public Updater(ObservableValue<V> observableValue, Function<V, P> converter) {
            this.observableValue = observableValue;
            this.converter = converter;
        }

        /**
         * 注册一个观察者，它将通过应用指定的转换器使 observableValue 和 GUI-Property 保持同步。
         *
         * @param property 当 observableValue 改变时将更新的 GUI-Property
         */
        public void update(Property<? super P> property) {
            observableValue.onChange((oldValue, newValue) -> {
                P convertedValue = converter.apply(newValue);
                Platform.runLater(() -> property.setValue(convertedValue));
            });
        }
    }

    default <V> ActionTrigger<V> onChangeOf(Property<V> property) {
        return new ActionTrigger<>(property);
    }

    default ActionTrigger<Double> onChangeOf(DoubleProperty property) {
        return new ActionTrigger<>(property);
    }

    default ActionTrigger<Integer> onChangeOf(IntegerProperty property) {
        return new ActionTrigger<>(property);
    }

    class ActionTrigger<V> {

        public ActionTrigger(Property<? super V> property) {
            this.property = property;
        }

        private Property<? super V> property;

        public void triggerAction(Consumer<V> action) {
            property.addListener((observableValue, oldValue, newValue) -> action.accept((V) newValue));
        }
    }

}

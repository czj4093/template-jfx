package io.czj.mvc.util;

import io.czj.mvc.util.ControllerBase;

import java.util.Objects;

/**
 * 投影仪是 GUI 的通用界面。
 *
 * See Dierk Koenig's conference talk: https://jaxenter.de/effiziente-oberflaechen-mit-dem-projektor-pattern-42119
 *
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
interface Projector<M, C extends ControllerBase<M>> {

    /**
     * 需要在 UI 部分的构造函数中调用
     */
    default void init(C controller) {
        Objects.requireNonNull(controller);
        initializeSelf();
        initializeParts();
        setupUiToActionBindings(controller);
        setupModelToUiBindings(controller.getModel());
    }

    /**
     * 初始化 UI 部分本身需要做的一切。 对于 GUI，加载样式表文件或附加字体是典型示例。
     */
    default void initializeSelf(){
    }

    /**
     * 完全初始化所有必要的 UI 元素（如 GUI 上的按钮、文本字段等）
     */
    void initializeParts();


    /**
     * 如果用户与 UI 交互，则在 Controller 上触发一些操作。此任务无需访问模型。
     *
     * 所有 EventHandler 将调用 Controller 上的单个方法。如果你要调用多个方法，你应该在 Controller 上引入一个新方法。
     */
    default void setupUiToActionBindings(C controller) {
    }

    /**
     * 每当 'model' 中的 'ObservableValue' 发生变化时，都必须更新 UI。此任务无需访问控制器。 在此处注册所有必要的观察员。
     */
    default void setupModelToUiBindings(M model) {
    }

}

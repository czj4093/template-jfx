package io.czj.mvc.templateapp.view;

import io.czj.mvc.templateapp.controller.SomeController;
import io.czj.mvc.templateapp.model.SomeModel;
import io.czj.mvc.util.ViewMixin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public class SomeGUI extends BorderPane implements ViewMixin<SomeModel, SomeController> { //所有 GUI 元素都必须实现 ViewMixin

    // fontawesome 字体中灯泡图标的 unicode
    private static final String LIGHT_BULB = "\uf0eb";

    // 声明您需要的所有 UI 元素
    private Button ledButton;
    private Button increaseButton;
    private Label counterLabel;
    private Label  infoLabel;

    public SomeGUI(SomeController controller) {
        //不要忘记调用'init'
        init(controller);
    }

    @Override
    public void initializeSelf() {
        //load all fonts you need
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");

        //apply your style
        addStylesheetFiles("/mvc/templateapp/style.css");

        getStyleClass().add("root-pane");
    }

    @Override
    public void initializeParts() {
        ledButton = new Button(LIGHT_BULB);
        ledButton.getStyleClass().add("icon-button");

        increaseButton = new Button("+");

        counterLabel = new Label();
        counterLabel.getStyleClass().add("counter-label");

        infoLabel = new Label();
        infoLabel.getStyleClass().add("info-label");
    }

    @Override
    public void layoutParts() {
        HBox topBox = new HBox(ledButton);
        topBox.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(counterLabel, increaseButton);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(true);
        centerBox.setPadding(new Insets(30));

        setTop(topBox);
        setCenter(centerBox);
        setBottom(infoLabel);
    }

    @Override
    public void setupUiToActionBindings(SomeController controller) {
        // look at that: all EventHandlers just trigger an action on 'controller'
        // by calling a single method

        increaseButton.setOnAction  (event -> controller.increaseCounter());
        ledButton.setOnMousePressed (event -> controller.setLedGlows(true));
        ledButton.setOnMouseReleased(event -> controller.setLedGlows(false));
    }

    @Override
    public void setupModelToUiBindings(SomeModel model) {
        onChangeOf(model.systemInfo)                       // the value we need to observe, in this case that's an ObservableValue<String>, no need to convert it
                .update(infoLabel.textProperty());         // keeps textProperty and systemInfo in sync

        onChangeOf(model.counter)                          // the value we need to observe, in this case that's an ObservableValue<Integer>
                .convertedBy(String::valueOf)              // we have to convert the Integer to a String
                .update(counterLabel.textProperty());      // keeps textProperty and counter in sync
    }

}

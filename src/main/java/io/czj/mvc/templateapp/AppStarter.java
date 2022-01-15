package io.czj.mvc.templateapp;

import io.czj.mvc.templateapp.controller.SomeController;
import io.czj.mvc.templateapp.model.SomeModel;
import io.czj.mvc.templateapp.view.SomeGUI;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @Author: chenzejin
 * @Date: 2022/1/15
 */
public class AppStarter extends Application {

    private SomeController controller;
//    private SomePUI        pui;

    @Override
    public void start(Stage primaryStage) {
        // 那是您的“信息中心”。
        SomeModel model = new SomeModel();
        controller = new SomeController(model);

        //both gui and pui are working on the same controller
//        pui = new SomePUI(controller, Pi4JContext.createContext());

        Parent gui = new SomeGUI(controller);

        Scene scene = new Scene(gui);

        primaryStage.setTitle("GUI of a Pi4J App");
        primaryStage.setScene(scene);

        primaryStage.show();

        // on desktop it's convenient to have a very basic emulator for the PUI to test the interaction between GUI and PUI
        // startPUIEmulator(new SomePuiEmulator(controller));
    }

    @Override
    public void stop() {
        controller.shutdown();
//        pui.shutdown();
    }

    private void startPUIEmulator(Parent puiEmulator) {
        Scene emulatorScene  = new Scene(puiEmulator);
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("PUI Emulator");
        secondaryStage.setScene(emulatorScene);
        secondaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);  //start the whole application
    }

}

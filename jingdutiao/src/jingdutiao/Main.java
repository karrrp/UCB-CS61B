package jingdutiao;

import javafx.stage.Stage;
import jingdutiao.ui.MainUI;
import javafx.application.Application;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainUI ui = new MainUI();
        ui.start(primaryStage); // 启动用户界面
    }

    public static void main(String[] args) {
        launch(args);
    }
}

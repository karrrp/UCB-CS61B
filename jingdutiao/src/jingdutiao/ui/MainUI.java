package jingdutiao.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainUI extends Application {

    private ProgressBar progressBar;
    private TextField stepField;
    private int totalSteps = 0; // 步骤总数
    private double[] workHours; // 存储步骤工时
    private String[] stepNames; // 存储步骤名称

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("目标管理小程序");

        // 创建布局
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // 步骤输入框
        stepField = new TextField();
        stepField.setPromptText("输入步骤名称和工时（格式：步骤名,工时）");
        grid.add(stepField, 0, 0);

        // 进度条
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(600); // 设置进度条宽度
        grid.add(progressBar, 0, 1);

        // 提交按钮
        Button submitButton = new Button("提交");
        submitButton.setOnAction(e -> initializeSteps());
        grid.add(submitButton, 0, 2);

        // 设置场景
        Scene scene = new Scene(grid, 700, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeSteps() {
        String stepsInput = stepField.getText();
        if (!stepsInput.isEmpty()) {
            String[] steps = stepsInput.split(","); // 分割输入
            totalSteps = steps.length;
            workHours = new double[totalSteps];
            stepNames = new String[totalSteps];

            // 创建步骤名称和工时
            StringBuilder stepDisplay = new StringBuilder();
            for (int i = 0; i < totalSteps; i++) {
                String[] parts = steps[i].trim().split("\\s+"); // 以空格分割
                stepNames[i] = parts[0]; // 步骤名称
                workHours[i] = Double.parseDouble(parts[1]); // 步骤工时
                stepDisplay.append(stepNames[i]).append(" (").append(workHours[i]).append("h)  ");
            }

            // 创建显示步骤的文本
            Text stepText = new Text(stepDisplay.toString());
            stepText.setWrappingWidth(600); // 设置文本宽度
            stepText.setStyle("-fx-font-size: 14px;");

            // 在进度条上方添加步骤显示
            VBox vbox = new VBox(stepText, progressBar);
            Scene scene = new Scene(vbox, 700, 200);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.show();
        } else {
            System.out.println("请输入步骤信息！");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

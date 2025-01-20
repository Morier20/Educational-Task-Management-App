package org.example.demo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 723, 541);
        stage.setTitle("Task management");
        stage.setScene(scene);
        stage.setResizable(false);
        HelloController controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.handleCloseRequest();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
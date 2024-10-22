package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Starter extends Application {
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"))));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo.png")));
        primaryStage.setTitle("TeeCeylon");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}

package com;

import com.controller.ServerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ServerApplication extends Application {

    private ServerController serverController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/com/controller/server.fxml"));
        Parent root = loader.load();
        serverController = loader.getController();
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/icon.gif").toString()));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Chat Server");
        primaryStage.show();
    }

    @Override
    public void stop() {
        serverController.shutdown();
        Platform.exit();
    }
}
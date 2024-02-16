package com;

import com.common.CommonSettings;
import com.controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    private ClientController clientController;

    public static void main(String[] args) {
        launch(ClientApplication.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/com/controller/chatclient.fxml"));
        Parent root = loader.load();

        clientController = loader.getController();

        primaryStage.getIcons().add(new Image(getClass().getResource("/images/icon.gif").toString()));
        primaryStage.setTitle(CommonSettings.PRODUCT_NAME);
        primaryStage.setScene(new Scene(root, 778, 575));
        primaryStage.setResizable(false);
        primaryStage.show();

        clientController.openLoginWindow();
    }

    @Override
    public void stop() {
        clientController.shutdown();
    }
}
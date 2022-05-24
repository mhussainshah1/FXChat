package com;

import com.common.CommonSettings;
import com.controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {

    private static Stage loginStage = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/com/controller/chatclient.fxml"));
        Parent root = loader.load();
        ClientController clientController = loader.getController();
        primaryStage.getIcons().add(new Image(getClass().getResource( "/images/icon.gif").toString()));
        primaryStage.setScene(new Scene(root, 778, 575));
        primaryStage.setTitle(CommonSettings.PRODUCT_NAME);
        primaryStage.show();

        createLoginStage();
        clientController.openLoginWindow();
    }

    public void createLoginStage() throws IOException {
        loginStage = new Stage();
        loginStage.getIcons().add(new Image(getClass().getResource( "/images/icon.gif").toString()));
        loginStage.setTitle(CommonSettings.PRODUCT_NAME + " - Login");
        loginStage.setAlwaysOnTop(true);
        loginStage.setResizable(false);
        loginStage.initModality(Modality.APPLICATION_MODAL);
    }

    public static Stage getLoginStage() {
        return loginStage;
    }
}
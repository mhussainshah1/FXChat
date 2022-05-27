package com;

import com.common.CommonSettings;
import com.controller.ClientController;
import com.controller.LoginController;
import com.controller.PrivateChatController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {

    private static String  userName;
    private static ClientController clientController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/com/controller/chatclient.fxml"));
        Parent root = loader.load();

        clientController = loader.getController();

        primaryStage.getIcons().add(new Image(getClass().getResource( "/images/icon.gif").toString()));
        primaryStage.setScene(new Scene(root, 778, 575));
        primaryStage.setTitle(CommonSettings.PRODUCT_NAME);
        primaryStage.show();

        showLoginStage();
        clientController.openLoginWindow();
    }

    public void showLoginStage() throws IOException {
        var loader = new FXMLLoader(ClientApplication.class.getResource("/com/controller/login.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setChatClientController(clientController);
        clientController.setLoginController(loginController);

        Stage loginStage = new Stage();
        loginStage.getIcons().add(new Image(ClientApplication.class.getResource( "/images/icon.gif").toString()));
        loginStage.setTitle(CommonSettings.PRODUCT_NAME + " - Login");
        loginStage.setAlwaysOnTop(true);
        loginStage.setResizable(false);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setScene(new Scene(root, 250, 400));
        loginStage.show();
    }

    public static void showPrivateChatStage() throws IOException {
        var loader = new FXMLLoader(ClientApplication.class.getResource("/com/controller/privatechat.fxml"));
        Parent root = loader.load();

        PrivateChatController privateChatController = loader.getController();
        privateChatController.setClient(clientController.getClient());

        Stage privateChatStage = new Stage();
        privateChatStage.getIcons().add(new Image(ClientApplication.class.getResource( "/images/icon.gif").toString()));
        privateChatStage.setScene(new Scene(root, 310, 270));
        privateChatStage.setTitle("Private Chat with - " + userName);
        privateChatStage.show();
    }

    public static void setUserName(String userName) {
        ClientApplication.userName = userName;
    }


}
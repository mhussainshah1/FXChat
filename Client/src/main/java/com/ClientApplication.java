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

import static com.common.CommonSettings.PRIVATE_WINDOW_HEIGHT;
import static com.common.CommonSettings.PRIVATE_WINDOW_WIDTH;

public class ClientApplication extends Application {

    private static String userName;
    private static ClientController clientController;

    public static void main(String[] args) {
        launch(args);
    }

    public static void showLoginStage() throws IOException {
        var loader = new FXMLLoader(ClientApplication.class.getResource("/com/controller/login.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setChatClientController(clientController);
        clientController.setLoginController(loginController);

        Stage loginStage = new Stage();
        loginStage.getIcons().add(new Image(ClientApplication.class.getResource("/images/icon.gif").toString()));
        loginStage.setTitle(CommonSettings.PRODUCT_NAME + " - Login");
        loginStage.setAlwaysOnTop(true);
        loginStage.setResizable(false);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setScene(new Scene(root, 250, 400));
        loginStage.show();
    }

    public static void showSignupStage() throws IOException {
        var loader = new FXMLLoader(ClientApplication.class.getResource("/com/controller/signup.fxml"));
        Parent root = loader.load();

/*        LoginController loginController = loader.getController();
        loginController.setChatClientController(clientController);
        clientController.setLoginController(loginController);*/

        Stage signupStage = new Stage();
        signupStage.getIcons().add(new Image(ClientApplication.class.getResource("/images/icon.gif").toString()));
        signupStage.setTitle(CommonSettings.PRODUCT_NAME + " - Sign Up");
        signupStage.setAlwaysOnTop(true);
        signupStage.setResizable(false);
        signupStage.initModality(Modality.APPLICATION_MODAL);
        signupStage.setScene(new Scene(root, 250, 400));
        signupStage.show();
    }

    public static PrivateChatController showPrivateChatStage(String selectedUser) throws IOException {
        var loader = new FXMLLoader(ClientApplication.class.getResource("/com/controller/privatechat.fxml"));
        Parent root = loader.load();

        PrivateChatController privateChatController = loader.getController();
        privateChatController.setClientController(clientController);
        privateChatController.setUserName(selectedUser);

        Stage privateChatStage = new Stage();
        privateChatStage.getIcons().add(new Image(ClientApplication.class.getResource("/images/icon.gif").toString()));
        privateChatStage.setScene(new Scene(root));
        privateChatStage.setHeight(PRIVATE_WINDOW_HEIGHT);
        privateChatStage.setWidth(PRIVATE_WINDOW_WIDTH);
        privateChatStage.setTitle("Private Chat with - " + selectedUser);
        privateChatStage.setResizable(false);
//        privateChatStage.setOnHidden(e->privateChatController.exitPrivateWindow());
        return privateChatController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/com/controller/chatclient.fxml"));
        Parent root = loader.load();

        clientController = loader.getController();

        primaryStage.getIcons().add(new Image(getClass().getResource("/images/icon.gif").toString()));
        primaryStage.setScene(new Scene(root, 778, 575));
        primaryStage.setTitle(CommonSettings.PRODUCT_NAME);
        primaryStage.setResizable(false);
        primaryStage.show();

        clientController.openLoginWindow();
    }

    @Override
    public void stop() {
        clientController.shutdown();
    }
}
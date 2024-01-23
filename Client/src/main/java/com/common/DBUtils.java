package com.common;

import com.controller.ClientController;
import com.controller.LoginController;
import com.controller.SignUpController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DBUtils {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String room, ClientController clientController) {
        Parent root = null;
        try {
            var loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            root = loader.load();
            if(fxmlFile.equals("/com/controller/signup.fxml")){
                SignUpController signUpController = loader.getController();
                signUpController.setClientController(clientController);
//                clientController.setSignUpController(signUpController);
            } else {
                LoginController loginController = loader.getController();
                loginController.setClientController(clientController);
                //clientController.setLoginController(loginController);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 250, 400));
        stage.show();
    }
}

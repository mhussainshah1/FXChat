package com.service;

import com.controller.ClientController;
import com.controller.LoginController;
import com.controller.SignUpController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StageService {

    private ClientController clientController;

    @FXML
    public void changeScene(ActionEvent event, String fxmlFile, String title, String username, String room) {
        Parent root = null;
        try {
            var loader = new FXMLLoader(StageService.class.getResource(fxmlFile));
            root = loader.load();
            if (fxmlFile.equals("/com/controller/signup.fxml")) {
                SignUpController signUpController = loader.getController();
                signUpController.setClientController(clientController);
            } else {
                LoginController loginController = loader.getController();
                loginController.setClientController(clientController);
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

    public ClientController getClientController() {
        return clientController;
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }
}

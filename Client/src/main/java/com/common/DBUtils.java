package com.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DBUtils {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String room) {
        Parent root = null;

        if (username != null && room != null) {
            try {
                var loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
//                WelcomeController welcomeController = loader.getController();
//                welcomeController.setUserInformation(username, favChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Scene scene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 250, 400));
        stage.show();
    }
}

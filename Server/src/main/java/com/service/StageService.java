package com.service;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StageService {
    @Autowired
    private ConfigurableApplicationContext springContext;

    @FXML
    public void changeScene(ActionEvent event, String fxmlFile, String title, String username, String room) {
        Parent root = null;

        if (username != null && room != null) {
            try {
                var loader = new FXMLLoader(getClass().getResource(fxmlFile));
                loader.setControllerFactory(springContext::getBean);
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(getClass().getResource(fxmlFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Scene scene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}

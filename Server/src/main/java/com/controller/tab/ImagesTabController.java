package com.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImagesTabController {

    private final ServerController serverController;

    private final BottomController bottomController;
    @Autowired
    public ImagesTabController(ServerController serverController, BottomController bottomController) {
        this.serverController = serverController;
        this.bottomController = bottomController;
    }
    @FXML
    private void iconHandler(MouseEvent mouseEvent) {
        Label icon = (Label) mouseEvent.getSource();

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
            icon.setStyle("-fx-border-color: black");

        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            bottomController.getTxtMessage().appendText("~~" + icon.getText() + " ");

        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
            icon.setStyle("-fx-border-color: white");
        }
    }
}
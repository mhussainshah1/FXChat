package com.controller.tab;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImagesTabController {
    private final TabPaneManagerController tabPaneManagerController;
    @FXML
    private BorderPane imagesTab;

    @Autowired
    public ImagesTabController(TabPaneManagerController tabPaneManagerController) {
        this.tabPaneManagerController = tabPaneManagerController;
    }

    @FXML
    private void iconHandler(MouseEvent mouseEvent) {
        Label icon = (Label) mouseEvent.getSource();

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
            icon.setStyle("-fx-border-color: black");

        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            tabPaneManagerController.getTxtMessage().appendText("~~" + icon.getText() + " ");

        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
            icon.setStyle("-fx-border-color: white");
        }
    }

    public void control(boolean status){
        imagesTab.setDisable(!status);
    }
}
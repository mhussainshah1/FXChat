package com.controller.tab;

import com.controller.CenterController;
import com.controller.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TabPaneManagerController {

    @Autowired
    private ConnectionTabController connectionTabController;
    @Autowired
    private CenterController centerController;
    @Autowired
    private MainController mainController;
    @FXML
    private Tab imagesTab;

    public void control(boolean status) {
        connectionTabController.control(status);
        imagesTab.setDisable(!status);
    }

    public void sendMessage(String message) {
        connectionTabController.sendMessage(message);
    }

    public void enableLogin() {
        mainController.enableLogin();
    }

    public void disableLogout() {
        mainController.disableLogout();
    }

    public void display(String string, Integer messageType) {
        centerController.display(string,messageType);
    }
}
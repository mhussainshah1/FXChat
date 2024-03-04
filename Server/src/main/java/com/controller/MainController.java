package com.controller;

import com.controller.tab.ConnectionTabController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainController {
    @Autowired
    CenterController centerController;
    @FXML
    private Tab imagesTab;
    @Autowired
    private BottomController bottomController;
    @Autowired
    private ConnectionTabController connectionTabController;

    //Methods

    //Handlers
    public void enableLogin() {
        control(true);
    }

    public void disableLogout() {
        control(false);
    }

    void control(boolean status) {
        centerController.control(status);

        connectionTabController.control(status);
        imagesTab.setDisable(!status);

        bottomController.control(status);
    }
}
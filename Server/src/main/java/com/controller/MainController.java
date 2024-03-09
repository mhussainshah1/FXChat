package com.controller;

import com.controller.tab.TabPaneManagerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainController {
    @Autowired
    private CenterController centerController;
    @Autowired
    private BottomController bottomController;
    @Autowired
    private TabPaneManagerController tabPaneManagerController;

    //Methods
    public void enableLogin() {
        control(true);
    }

    public void disableLogout() {
        control(false);
    }

    void control(boolean status) {
        centerController.control(status);
        tabPaneManagerController.control(status);
        bottomController.control(status);
    }
}
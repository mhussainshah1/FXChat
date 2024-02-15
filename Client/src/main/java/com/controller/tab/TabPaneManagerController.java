package com.controller.tab;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TabPaneManagerController {

    @Autowired
    UsersTabController usersTabController;
    @Autowired
    RoomsTabController roomsTabController;
    @Autowired
    ImagesTabController imagesTabController;
    @FXML
    private BorderPane userstab;
    @FXML
    private BorderPane roomstab;
    @FXML
    private BorderPane imagestab;
    @FXML
    private TabPane rightPane;

}

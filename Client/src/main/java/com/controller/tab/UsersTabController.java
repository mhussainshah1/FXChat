package com.controller.tab;

import com.controller.ClientController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsersTabController {
    @Autowired
    ClientController clientController;
    @FXML
    private Tab userstab;
    @FXML
    private Button btnIgnoreUser;
    @FXML
    private ListView<Label> userView;
    private String selectedUser;

    @FXML
    private void initialize() {
        selectedUser = "";
    }

    @FXML
    private void listViewHandler(MouseEvent mouseEvent) {
    }

    @FXML
    private void btnHandler(ActionEvent actionEvent) {
    }
}

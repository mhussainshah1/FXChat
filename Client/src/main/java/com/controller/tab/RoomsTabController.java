package com.controller.tab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Component;

@Component
public class RoomsTabController {
    @FXML
    private Tab roomstab;
    @FXML
    private ListView<Label> roomView;
    @FXML
    private TextField txtUserCount;
    @FXML
    private Button btnChangeRoom;
    @FXML
    private void listViewHandler(MouseEvent mouseEvent) {
        txtUserCount.setText("");
    }

    @FXML
    private void btnHandler(ActionEvent e) {
        var button = (Button) e.getTarget();
        var name = button.getText();
    }
}

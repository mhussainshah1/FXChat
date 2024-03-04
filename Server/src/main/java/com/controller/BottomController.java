package com.controller;

import com.controller.tab.ConnectionTabController;
import com.controller.tab.TabPaneManagerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
public class BottomController {
    @FXML
    private TextField txtMessage;
    @FXML
    private Button btnSendMessage;

    private final TabPaneManagerController tabPaneManagerController;

    @Autowired
    public BottomController(TabPaneManagerController tabPaneManagerController) {
        this.tabPaneManagerController = tabPaneManagerController;
    }


    @FXML
    public void btnHandler(ActionEvent e) {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Send Message!")) {
            if (!txtMessage.getText().isEmpty())
                sendMessage(txtMessage.getText());
        }
    }

    @FXML
    public void txtHandler(ActionEvent e) {
        btnSendMessage.fire();
    }

    private void sendMessage(String message) {
        tabPaneManagerController.sendMessage(message);
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    public TextField getTxtMessage() {
        return txtMessage;
    }

    void control(boolean status) {
        txtMessage.setDisable(!status);
        btnSendMessage.setDisable(!status);
    }
}

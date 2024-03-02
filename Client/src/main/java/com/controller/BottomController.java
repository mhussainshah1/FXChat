package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BottomController {
    @Autowired
    MainController mainController;
    @FXML
    private Button btnSend;
    @FXML
    private TextField txtMessage;

    //Event Handlers
    @FXML
    private void btnHandler(ActionEvent e) throws IOException {
        var button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Send Message!")) {
            if (!txtMessage.getText().isEmpty()) {
                sendMessage();
            }
        } else if (name.equals("Exit Chat")) {
            mainController.shutdown();
        }
    }

    @FXML
    private void txtHandler(ActionEvent e) {
        btnSend.fire();
    }

    // To send a message to the text flow
    void sendMessage() throws IOException {
        mainController.sendMessage(txtMessage.getText());
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    void control(boolean status) {
        txtMessage.setEditable(status);
        btnSend.setDisable(!status);
    }

    public TextField getTxtMessage() {
        return txtMessage;
    }
}

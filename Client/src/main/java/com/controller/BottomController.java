package com.controller;

import com.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.common.CommonSettings.MESSAGE_TYPE_DEFAULT;

@Component
public class BottomController {
    @FXML
    Button btnSend;
    @FXML
    private TextField txtMessage;
    @Autowired
    ClientController clientController;

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
            clientController.shutdown();
        }
    }

    @FXML
    private void txtHandler(ActionEvent e) {
        btnSend.fire();
    }

    // To send a message to the text flow
    void sendMessage() throws IOException {
        clientController.sendMessage(txtMessage.getText());
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    public TextField getTxtMessage() {
        return txtMessage;
    }
}

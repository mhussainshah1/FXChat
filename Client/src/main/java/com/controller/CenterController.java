package com.controller;

import com.client.Message;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.TextFlow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CenterController {
    @FXML
    private TextFlow messageBoard;
    @FXML
    private ScrollPane sp_main;
    private Message message;

    @FXML
    private void initialize(){
        message = new Message(new Label());
        messageBoard.heightProperty().addListener((observable, oldValue, newValue) -> sp_main.setVvalue((Double) newValue));
    }
    public TextFlow getMessageBoard() {
        return messageBoard;
    }

    //Function To Update the Information Label
    public void display(String message, Integer type) {
        List<Node> nodes = this.message.parseMessage(message, type);
        messageBoard.getChildren().addAll(nodes);
    }
}

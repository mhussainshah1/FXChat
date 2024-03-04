package com.controller;

import com.common.Message;
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
    private ScrollPane sp_main;
    @FXML
    private TextFlow messageBoard;
    private Message message;
    @FXML
    public void initialize() {
        this.message = new Message(new Label());
        messageBoard.heightProperty().addListener((observable, oldValue, newValue) -> sp_main.setVvalue((Double) newValue));
    }

    // Display an event to the console
    public void display(String text, int type) {
        List<Node> nodes = message.parseMessage(text, type);
        messageBoard.getChildren().addAll(nodes);
    }

    void control(boolean status) {
        messageBoard.getChildren().clear();
    }

}

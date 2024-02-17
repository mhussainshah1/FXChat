package com.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.TextFlow;
import org.springframework.stereotype.Component;

@Component
public class CenterController {

    @FXML
    private TextFlow messageBoard;
    @FXML
    private ScrollPane sp_main;

    @FXML
    private void initialize(){
        messageBoard.heightProperty().addListener((observable, oldValue, newValue) -> sp_main.setVvalue((Double) newValue));
    }
    public TextFlow getMessageBoard() {
        return messageBoard;
    }
}

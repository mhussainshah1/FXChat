package com.controller;

import com.common.Message;
import com.controller.tab.ConnectionTabController;
import com.server.ChatServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
public class ServerController {
    @FXML
    private Tab imagesTab;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private TextFlow messageBoard;
    private Message message;
    @Autowired
    private BottomController bottomController;
    @Autowired
    private ConnectionTabController connectionTabController;

    //Methods
    @FXML
    public void initialize() {
        this.message = new Message(new Label());
        messageBoard.heightProperty().addListener((observable, oldValue, newValue) -> sp_main.setVvalue((Double) newValue));
    }

    //Handlers

    public void enableLogin() {
        control(true);
    }

    public void disableLogout() {
        control(false);
    }

    void control(boolean status) {
        messageBoard.getChildren().clear();

        connectionTabController.control(status);
        imagesTab.setDisable(!status);

        bottomController.control(status);
    }

    // Display an event to the console
    public void display(String text, int type) {
        List<Node> nodes = message.parseMessage(text, type);
        messageBoard.getChildren().addAll(nodes);
    }
}
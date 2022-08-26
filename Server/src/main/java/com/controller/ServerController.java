package com.controller;

import com.common.Message;
import com.server.ChatServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.List;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;


public class ServerController {
    public ScrollPane sp_main;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnStart;
    @FXML
    private TextField txtServerPort;
    @FXML
    private TextField txtMaximumGuest;
    @FXML
    private TextFlow messageBoard;
    @FXML
    private TextField txtMessage;
    @FXML
    private Button btnSendMessage;
    private int serverPort = 1436;
    private int maximumGuestNumber = 50;
    private Message message;
    private ChatServer server;

    //Methods
    public void initialize() throws IOException {
        this.message = new Message(new Label());
        txtServerPort.setText(String.valueOf(serverPort));
        txtMaximumGuest.setText(String.valueOf(maximumGuestNumber));
        messageBoard.heightProperty().addListener((observable, oldValue, newValue) -> sp_main.setVvalue((Double) newValue));
    }

    //Handlers
    public void btnHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Start Server")) {
            server = createServer();
            server.startConnection();
            enableLogin();
            display("About to accept client connection...", MESSAGE_TYPE_ADMIN);
        } else if (name.equals("Send Message!")) {
            if (!txtMessage.getText().isEmpty())
                sendMessage(txtMessage.getText());
        } else if (name.equals("Stop Server")) {
            shutdown();
        }
    }

    public void shutdown() {
        if (server != null)
            server.closeConnection();
        disableLogout();
    }

    public void txtHandler(ActionEvent e) {
        btnSendMessage.fire();
    }

    private void sendMessage(String message) {
        server.broadcast(message);
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    private void enableLogin() {
        control(true);
    }

    private void disableLogout() {
        control(false);
    }

    void control(boolean status) {
        messageBoard.getChildren().clear();
        txtServerPort.setDisable(status);
        txtMaximumGuest.setDisable(status);
        btnStart.setDisable(status);
        btnStop.setDisable(!status);
        txtMessage.setDisable(!status);
        btnSendMessage.setDisable(!status);
    }

    private ChatServer createServer() throws IOException {
        return new ChatServer(serverPort, maximumGuestNumber, ( data , messageType) -> {
            Platform.runLater(() -> { //UI or background thread - manipulate UI object , It gives control back to UI thread
                display(data.toString(), messageType);
            });
        });
    }

    // Display an event to the console
    public void display(String text, int type) {
        List<Node> nodes = this.message.parseMessage(text, type);
        messageBoard.getChildren().addAll(nodes);
    }
}
package com.controller;

import com.common.Message;
import com.common.Data;
import com.server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.List;

import static com.common.Message.MESSAGE_TYPE_ADMIN;

public class ServerController {

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
    //Handlers
    private Server server = createServer();
    private int serverPort = 1436;
    private int maximumGuestNumber;
    private Message message;

    //Methods
    public void initialize() throws IOException {
        this.message = new Message();
        try(Data data = new Data("server.properties");){
            this.serverPort = data.getServerPort();
            this.maximumGuestNumber = data.getMaximumGuestNumber();
        }
        txtServerPort.setText(String.valueOf(serverPort));
        txtMaximumGuest.setText(String.valueOf(maximumGuestNumber));
    }

    public void btnHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Start Server")) {
            try(Data data = new Data("server.properties")){
                data.setServerPort(Integer.parseInt(txtServerPort.getText()));
                data.setMaximumGuestNumber(Integer.parseInt(txtMaximumGuest.getText()));
            }
            server = createServer();
            server.startConnection();
            enableLogin();
        }

        else if (name.equals("Send Message!")) {
            if(!txtMessage.getText().isEmpty())
                sendMessage("Server : " + txtMessage.getText());

        }

        else if (name.equals("Stop Server")) {
            server.closeConnection();
            disableLogout();
        }
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

    private Server createServer() {
        return new Server(serverPort, data -> {
            Platform.runLater(() -> { //UI or background thread - manipulate UI object , It gives control back to UI thread
                display(data.toString(),MESSAGE_TYPE_ADMIN);
            });
        });
    }

    // Display an event to the console
    public void display(String message, int type) {
        System.out.println(message);
        List<Node> nodes = this.message.parseMessage(message, type);
        messageBoard.getChildren().addAll(nodes);
    }
}
package com.controller.tab;

import com.controller.CenterController;
import com.controller.MainController;
import com.server.ChatServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
public class ConnectionTabController {
    private final String serverName = "localhost";
    private final int serverPort = 1436;
    private final int maximumGuestNumber = 50;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnStart;
    @FXML
    private TextField txtServerName;
    @FXML
    private TextField txtServerPort;
    @FXML
    private TextField txtMaximumGuest;
    private ChatServer server;
    @Autowired
    MainController serverController;
    @Autowired
    CenterController centerController;

    @FXML
    public void initialize() {
        txtServerName.setText(serverName);
        txtServerPort.setText(String.valueOf(serverPort));
        txtMaximumGuest.setText(String.valueOf(maximumGuestNumber));
    }
    @FXML
    public void btnHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Start Server")) {
            server = createServer();
            server.startConnection();
            serverController.enableLogin();
            centerController.display("About to accept client connection...", MESSAGE_TYPE_ADMIN);
        } if (name.equals("Stop Server")) {
            shutdown();
        }
    }

    public void control(boolean status) {
        txtServerName.setDisable(status);
        txtServerPort.setDisable(status);
        txtMaximumGuest.setDisable(status);
        btnStart.setDisable(status);
        btnStop.setDisable(!status);
    }

    private ChatServer createServer() throws IOException {
        return new ChatServer(
                txtServerPort.getText(),
                Integer.parseInt(txtServerPort.getText()),
                Integer.parseInt(txtMaximumGuest.getText()),
                (data, messageType) -> {
                    Platform.runLater(() -> { //UI or background thread - manipulate UI object , It gives control back to UI thread
                        centerController.display(data.toString(), messageType);
                    });
                });
    }

    public void shutdown() {
        if (server != null)
            server.closeConnection();
        serverController.disableLogout();
    }

    public void sendMessage(String message) {
        server.broadcast(message);
    }
}

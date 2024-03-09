package com.controller.tab;

import com.server.ChatServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
public class ConnectionTabController {

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
    @Autowired
    private ChatServer server;
    @Autowired
    private TabPaneManagerController tabPaneManagerController;

    @FXML
    public void initialize() {
        txtServerName.setText(server.getServerName());
        txtServerPort.setText(String.valueOf(server.getServerPort()));
        txtMaximumGuest.setText(String.valueOf(server.getMaximumGuestNumber()));
    }

    @FXML
    public void btnHandler(ActionEvent e) {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Start Server")) {
//            server = createServer();
            server.setServerName(txtServerName.getText());
            server.setServerPort(Integer.parseInt(txtServerPort.getText()));
            server.setMaximumGuestNumber(Integer.parseInt(txtMaximumGuest.getText()));
            server.startConnection();
            tabPaneManagerController.enableLogin();
            tabPaneManagerController.display("About to accept client connection...", MESSAGE_TYPE_ADMIN);
        }
        if (name.equals("Stop Server")) {
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

    public void shutdown() {
        if (server != null)
            server.closeConnection();
        tabPaneManagerController.disableLogout();
    }

    public void sendMessage(String message) {
        server.broadcast(message);
    }
}

package com.controller;

import com.common.Data;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Properties;

public class LoginController {
    @FXML
    private TextField txtUserName;
    @FXML
    private TextField txtServerName;
    @FXML
    private TextField txtServerPort;
    @FXML
    private TextField txtProxyHost;
    @FXML
    private TextField txtProxyPort;
    @FXML
    private CheckBox proxyCheckBox;
    private ClientController clientController;
    private boolean connect;
    private Properties properties;

    //Calls automatically
    @FXML
    private void initialize() throws IOException {

        try (Data data = new Data("data.properties")) {
            txtUserName.setText(data.getUserName());
            txtServerName.setText(data.getServerName());
            txtServerPort.setText(String.valueOf(data.getServerPort()));
            proxyCheckBox.setSelected(data.isProxyState());
            txtProxyHost.setText(data.getProxyHost());
            txtProxyPort.setText(String.valueOf(data.getProxyPort()));
        }
    }

    //Handler
    @FXML
    private void actionHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Connect")) {
            connect = true;
            try (var data = new Data("data.properties")) {
                data.setProxyState(proxyCheckBox.isSelected());
                data.setUserName(txtUserName.getText());
                data.setServerName(txtServerName.getText());
                System.out.println(txtServerPort.getText());
                data.setServerPort(Integer.parseInt(txtServerPort.getText()));
                data.setProxyHost(txtProxyHost.getText());
                data.setProxyPort(Integer.parseInt(txtProxyPort.getText()));
            }
            button.getScene().getWindow().hide();
            clientController.loginToChat();
        }

        else if (name.equals("Quit")) {
            connect = false;
            button.getScene().getWindow().hide();
        }
    }

    public boolean isConnect() {
        return connect;
    }

    public void setChatClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    public String getUserName() {
        return txtUserName.getText();
    }

    public String getServerName() {
        return txtServerName.getText();
    }

    public int getServerPort() {
        return Integer.parseInt(txtServerPort.getText());
    }

    public String getProxyHost() {
        return txtProxyHost.getText();
    }

    public int getProxyPort() {
        return Integer.parseInt(txtProxyPort.getText());
    }

    public boolean isProxyCheckBox() {
        return proxyCheckBox.isSelected();
    }
}
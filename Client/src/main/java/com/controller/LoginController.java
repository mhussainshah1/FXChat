package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.common.CommonSettings.PRODUCT_NAME;

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
    private ChatClientController chatClientController;
    private boolean connect;
    private Properties properties;

    //Calls automatically
    @FXML
    public void initialize() {

        properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("data.properties"));
        } catch (IOException | NullPointerException exc) {
            exc.printStackTrace();
        }
        properties.forEach((x, y) -> System.out.println(x + " = " + y));
        txtUserName.setText(properties.getProperty("UserName"));

        if (properties.getProperty("ServerName") != null)
            txtServerName.setText(properties.getProperty("ServerName"));
        else
            txtServerName.setText("bah.com");

        if (properties.getProperty("ServerPort") != null)
            txtServerPort.setText(properties.getProperty("ServerPort"));
        else
            txtServerPort.setText("1436");

        proxyCheckBox.setSelected(Boolean.parseBoolean(properties.getProperty("ProxyState")));
        txtProxyHost.setText(properties.getProperty("ProxyHost"));
        txtProxyPort.setText(properties.getProperty("ProxyPort"));
    }

    //Handler
    @FXML
    public void actionHandler(ActionEvent e) {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Connect")) {
            connect = true;
            try (var fileOutputStream = new FileOutputStream("data.properties")) {
                if (proxyCheckBox.isSelected())
                    properties.setProperty("ProxyState", "true");
                else
                    properties.setProperty("ProxyState", "false");

                properties.setProperty("UserName", txtUserName.getText());
                properties.setProperty("ServerName", txtServerName.getText());
                properties.setProperty("ServerPort", txtServerPort.getText());
                properties.setProperty("ProxyHost", txtProxyHost.getText());
                properties.setProperty("ProxyPort", txtProxyPort.getText());
                properties.store(fileOutputStream, PRODUCT_NAME);

                button.getScene().getWindow().hide();

                chatClientController.loginToChat();

            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }

        if (name.equals("Quit")) {
            connect = false;
            button.getScene().getWindow().hide();
        }
    }

    public boolean isConnect() {
        return connect;
    }

    public void setChatClientController(ChatClientController chatClientController) {
        this.chatClientController = chatClientController;
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
package controller;

import client.ClientObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static client.CommonSettings.PRODUCT_NAME;

public class LoginController {

    @FXML
    TextField txtUserName;

    @FXML
    TextField txtServerName;

    @FXML
    TextField txtServerPort;

    @FXML
    TextField txtProxyHost;

    @FXML
    TextField txtProxyPort;

    @FXML
    CheckBox proxyCheckBox;

    private boolean connect;
    private Properties properties;
    private ClientObject client;

    //Calls automatically
    @FXML
    public void initialize() {
        client = new ClientObject();
        properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("data.properties"));
        } catch (IOException | NullPointerException exc) {
            exc.printStackTrace();
        }
        properties.forEach((x,y) -> System.out.println(x + " = " + y));
        txtUserName.setText(properties.getProperty("UserName"));

        if (properties.getProperty("ServerName") != null)
            txtServerName.setText(properties.getProperty("ServerName"));
        else
            txtServerName.setText("india.dns2go.com");

        if (properties.getProperty("ServerPort") != null)
            txtServerPort.setText(properties.getProperty("ServerPort"));
        else
            txtServerPort.setText("1436");

        proxyCheckBox.setSelected(Boolean.parseBoolean(properties.getProperty("ProxyState")));
        txtProxyHost.setText(properties.getProperty("ProxyHost"));
        txtProxyPort.setText(properties.getProperty("ProxyPort"));
        loginToChat();
    }

    //Handler
    public void actionHandler(ActionEvent e) {
        var name = ((Button) e.getTarget()).getText();

        if (name.equals("Connect")) {
            connect =true;
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
            } catch (java.io.IOException exc) {
                exc.printStackTrace();
            }
        }
        if (name.equals("Quit")) {
            connect =false;
        }
    }

    public boolean isConnect() {
        return connect;
    }

    private void loginToChat() {

        if (connect) {
            client.setUserName(txtUserName.getText());
            client.setServerName(txtServerName.getText());
            client.setServerPort(Integer.parseInt(txtServerPort.getText()));
            if (proxyCheckBox.isSelected()) {
                client.setProxy(true);
                client.setProxyHost(txtProxyHost.getText());
                client.setServerPort(Integer.parseInt(txtProxyPort.getText()));
            } else {
                client.setProxy(false);
            }
        }
        System.out.println(client);
    }
}
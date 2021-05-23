package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static client.CommonSettings.PRODUCT_NAME;

public class ServerController {
    @FXML
    TextField txtMessage;
    @FXML
    Button btnStop;
    @FXML
    Button btnStart;
    @FXML
    TextField txtServerName;
    @FXML
    TextField txtServerPort;
    @FXML
    TextField txtMaximumGuest;
    @FXML
    Button btnSendMessage;
    private Properties properties;

    public void initialize() {
        properties = getProperties();
//        properties.forEach((x, y) -> System.out.println(x + " = " + y));

        if (properties.getProperty("ServerName") != null)
            txtServerName.setText(properties.getProperty("ServerName"));
        else
            txtServerName.setText("localhost");

        if (properties.getProperty("PortNumber") != null)
            txtServerPort.setText(properties.getProperty("PortNumber"));
        else
            txtServerPort.setText("1436");

        if (properties.getProperty("") != null)
            txtMaximumGuest.setText(properties.getProperty("MaximumGuest"));
        else
            txtMaximumGuest.setText("50");
    }

    //Handlers
    public void btnHandler(ActionEvent e) {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Start Server")) {
            try (var fileOutputStream = new FileOutputStream("server.properties")) {
                properties.setProperty("ServerName", txtServerName.getText());
                properties.setProperty("PortNumber", txtServerPort.getText());
                properties.setProperty("MaximumGuest", txtMaximumGuest.getText());
                properties.store(fileOutputStream, PRODUCT_NAME);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            enableLogin();
        }

        if (name.equals("Stop Server")) {
            disableLogout();
        }
    }

    //Methods
    private void enableLogin() {
        control(true);
    }

    private void disableLogout() {
        control(false);
    }

    void control(boolean status) {
        txtServerName.setDisable(status);
        txtServerPort.setDisable(status);
        txtMaximumGuest.setDisable(status);
        btnStart.setDisable(status);
        btnStop.setDisable(!status);
        txtMessage.setDisable(!status);
        btnSendMessage.setDisable(!status);
    }

    //Loading Properties File
    private Properties getProperties() {
        //Getting the Property Value From Property File
        properties = new Properties();
        try (var inputstream = this.getClass().getClassLoader().getResourceAsStream("server.properties")) {
            properties.load(inputstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
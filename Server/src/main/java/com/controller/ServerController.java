package com.controller;

import com.common.MessageObject;
import com.server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static com.common.CommonSettings.PRODUCT_NAME;
import static com.common.MessageObject.MESSAGE_TYPE_ADMIN;

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
    ;
    private Properties properties;
    private int portNumber = 1436;
    private int maximumGuestNumber;
    private String roomList = "";
    private MessageObject messageObject;

    //Methods
    public void initialize() {
        this.messageObject = new MessageObject();
        properties = getProperties();
        properties.forEach((x, y) -> System.out.println(x + " = " + y));

        if (properties.getProperty("PortNumber") != null)
            portNumber = Integer.parseInt(properties.getProperty("PortNumber"));
        else
            portNumber = 1436;

        txtServerPort.setText(String.valueOf(portNumber));

        if (properties.getProperty("") != null)
            maximumGuestNumber = Integer.parseInt(properties.getProperty("MaximumGuest"));
        else
            maximumGuestNumber = 50;
        txtMaximumGuest.setText(String.valueOf(maximumGuestNumber));

        if (properties.getProperty("roomlist") != null)
            roomList = properties.getProperty("roomlist");
        else
            roomList = "General;Teen;Music;Party;";
    }

    public void btnHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Start Server")) {
            try (var fileOutputStream = new FileOutputStream("server.properties")) {
                properties.setProperty("PortNumber", txtServerPort.getText());
                properties.setProperty("MaximumGuest", txtMaximumGuest.getText());
                properties.store(fileOutputStream, PRODUCT_NAME);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            server = createServer();
            server.startConnection();
            enableLogin();
        }

        if (name.equals("Send Message!")) {
            sendMessage("Server : " + txtMessage.getText());
        }

        if (name.equals("Stop Server")) {
            server.closeConnection();
            disableLogout();
        }
    }

    public void txtHandler(ActionEvent e) {
        sendMessage("Server : " + txtMessage.getText());
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

    //Loading Properties File
    private Properties getProperties() {
        //Getting the Property Value From Property File
        properties = new Properties();
        try (var inputstream = getClass().getClassLoader().getResourceAsStream("server.properties")) {
            properties.load(inputstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private Server createServer() {
        return new Server(portNumber, data -> {
            Platform.runLater(() -> { //UI or background thread - manipulate UI object , It gives control back to UI thread
                display(data.toString(),MESSAGE_TYPE_ADMIN);
            });
        });
    }

    // Display an event to the console
    public void display(String message, int type) {
        System.out.println(message);
        List<Node> nodes = messageObject.parseMessage(message, type);
        messageBoard.getChildren().addAll(nodes);
    }
}
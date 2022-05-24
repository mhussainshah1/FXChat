package com.controller;

import com.common.MessageObject;
import com.server.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

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
        properties = getProperties();
//        properties.forEach((x, y) -> System.out.println(x + " = " + y));

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

        if (name.equals("Stop Server")) {
            server.stop();
            disableLogout();
        }

        if (name.equals("Send Message!")) {
            String message = "Server : " + txtMessage.getText();
            server.broadcast(message);
            txtMessage.clear();
            txtMessage.requestFocus();
        }
    }

    public void txtHandler(ActionEvent e) {
        TextField textField = (TextField) e.getSource();
        String ID = textField.getId();
        if (ID.equals("txtMessage")) {
            btnSendMessage.fire();
        }
    }

    private void enableLogin() {
        control(true);
    }

    private void disableLogout() {
        control(false);
        messageBoard.getChildren().clear();
    }

    void control(boolean status) {
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
                //messageBoard.getChildren().add(new Text(data.toString() + "\n"));
                display(data.toString());
            });

           /* Platform.runLater(() -> {
                messageObject = new MessageObject();
                messageBoard.getChildren().add(messageObject.getText(data.toString(), MESSAGE_TYPE_ADMIN));
            });*/
        });
    }

    // Display an event to the console
    public void display(String message) {
        System.out.println(message);
        List<Node> list = new ArrayList<>();

        if (message.contains("~~")) {
            var tokenizer = new StringTokenizer(message, " ");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().trim();
                //If it's a Proper Image
                if (token.contains("~~")) {
                    int i = Integer.parseInt(token.substring(2));
                    if (i >= 0 && i < 21) {
                        var imageView = new ImageView(new Image(getClass().getResource("/icons/photo" + i + ".gif").toString()));
                        list.add(imageView);
                    }
                } else {
                    messageObject = new MessageObject();
                    list.add(messageObject.getText(token + " ", MESSAGE_TYPE_ADMIN));
                }
            }
        } else {
            messageObject = new MessageObject();
            list.add(messageObject.getText(message, MESSAGE_TYPE_ADMIN));
        }
        messageBoard.getChildren().addAll(list);
        messageBoard.getChildren().add(new Text("\n"));
    }
}
package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import server.Server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import static client.CommonSettings.PRODUCT_NAME;

public class ServerController {

    @FXML
    Button btnStop;
    @FXML
    Button btnStart;
    @FXML
    TextField txtServerPort;
    @FXML
    TextField txtMaximumGuest;
    @FXML
    TextFlow messageBoard;
    @FXML
    TextField txtMessage;
    @FXML
    Button btnSendMessage;
    //Handlers
    Server server;
    private Properties properties;
    private int portNumber;
    private int maximumGuestNumber;
    private String roomList = "";

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

    public void btnHandler(ActionEvent e) {
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
            server = new Server(portNumber, this);
            enableLogin();
        }

        if (name.equals("Stop Server")) {
            server.stop();
            disableLogout();
        }

        if (name.equals("Send Message!")) {
            String text;
            if (messageBoard.getChildren().size() == 0) {
                text = "Server : " + txtMessage.getText();
            } else { // Add new line if not the first child
                text = "Server : " + txtMessage.getText();
            }
            if (txtMessage.getText().contains("~~")) {
                var tokenizer = new StringTokenizer(txtMessage.getText(), " ");
                messageBoard.getChildren().add(new Text("Server : "));
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    //If its a Proper Image
                    int i = Integer.parseInt(token.substring(2));
                    var imageView = new ImageView(new Image("icons/photo" + i + ".gif"));
                    messageBoard.getChildren().addAll(imageView);
                }
                server.displayWithoutStamp("");
            } else {
                server.broadcast(text);
            }
            txtMessage.clear();
            txtMessage.requestFocus();
        }
    }

    //Methods
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
        try (var inputstream = this.getClass().getClassLoader().getResourceAsStream("server.properties")) {
            properties.load(inputstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public TextFlow getMessageBoard() {
        return messageBoard;
    }

    public void txtHandler(ActionEvent e) {
        TextField textField = (TextField) e.getSource();
        String ID = textField.getId();
        if (ID.equals("txtMessage")) {
            btnSendMessage.fire();
        }
    }
}
package controller;

import client.ChatClient;
import client.ClientObject;
import client.MessageObject;
import common.ChatMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static client.CommonSettings.COMPANY_NAME;
import static client.CommonSettings.PRODUCT_NAME;
import static client.MessageObject.MESSAGE_TYPE_ADMIN;
import static client.MessageObject.MESSAGE_TYPE_DEFAULT;

public class ChatClientController {

    public static final int QUIT_TYPE_DEFAULT = 0,
            QUIT_TYPE_KICK = 1,
            QUIT_TYPE_NULL = 2;
    @FXML
    TilePane tilePane;
    @FXML
    TextFlow messageBoard;
    @FXML
    Button btnSend;
    @FXML
    TextField txtMessage;
    @FXML
    TabPane rightPane;
    @FXML
    MenuItem loginMenuItem;
    @FXML
    MenuItem logoutMenuItem;

    String userName = "Amir";
    String userRoom = "General";
    int totalUserCount;
    ChatClient client;
    private LoginController loginController;
    private ClientObject clientData;
    private MessageObject messageObject;

    public void initialize() {
        clientData = new ClientObject();
        messageObject = new MessageObject();

        List<Button> buttonList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var icon = new Button(Integer.toString(i), new ImageView("icons/photo" + i + ".gif"));
            icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            icon.setOnAction(event -> txtMessage.appendText("~~" + icon.getText() + " "));
            icon.setCursor(Cursor.OPEN_HAND);
            buttonList.add(icon);
        }
        tilePane.setPrefColumns(3);
        tilePane.setHgap(15);
        tilePane.setVgap(15);
        tilePane.setPadding(new Insets(10));
        tilePane.getChildren().addAll(buttonList);
    }

    //Handlers
    public void menuHandler(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.NONE);
        var name = ((MenuItem) e.getTarget()).getText();

        if (name.equals("Login"))
            createLoginStage();

        if (name.equals("Logout"))
            disconnectChat();

        if (name.equals("Exit")) {
            disconnectChat();
            Platform.exit();
        }

        if (name.equals("About")) {
            alert.setTitle("About Us");
            alert.setHeaderText(PRODUCT_NAME);
            alert.setContentText("\nDeveloped By...\n" + COMPANY_NAME);
            alert.setGraphic(new ImageView("main/resources/icons/photo13.gif"));
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.show();
        }
    }

    private void sendMessage() {
        ChatMessage message = new ChatMessage(ChatMessage.MESSAGE, userName + ": " + txtMessage.getText());
//        sendMessageToServer("MESS " + userRoom + "~" + userName + ": " + txtMessage.getText());
        messageObject.getText(userName + ": " + txtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    //methods

    public void btnHandler(ActionEvent e) {
        var name = ((Button) e.getTarget()).getText();

        if (name.equals("Send Message!")) {
            if (!txtMessage.getText().trim().equals("")) {
                sendMessage();
            }
        }
        if (name.equals("Exit Chat")) {
            disconnectChat();
            Platform.exit();
        }
    }

    public void createLoginStage() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        loginController = loader.getController();
        Stage loginStage = new Stage();
        loginStage.getIcons().add(new Image("/images/icon.gif"));
        loginStage.setTitle(PRODUCT_NAME + " - Login");
        loginStage.setAlwaysOnTop(true);
        loginStage.setResizable(false);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setScene(new Scene(root, 250, 400));
        loginStage.show();
        loginToChat();
    }

    private void loginToChat() {
        if (loginController.isConnect()) {
            clientData.setUserName(loginController.txtUserName.getText());
            clientData.setServerName(loginController.txtServerName.getText());
            clientData.setServerPort(Integer.parseInt(loginController.txtServerPort.getText()));
            if (loginController.proxyCheckBox.isSelected()) {
                clientData.setProxy(true);
                clientData.setProxyHost(loginController.txtProxyHost.getText());
                clientData.setServerPort(Integer.parseInt(loginController.txtProxyPort.getText()));
            } else {
                clientData.setProxy(false);
            }
        }
        connectToServer();
        System.out.println(clientData);
    }

    private void connectToServer() {
        messageBoard.getChildren().clear();
        messageBoard.getChildren().add(messageObject.getText("Connecting To Server... Please Wait...", MESSAGE_TYPE_ADMIN));
        try {
            client = new ChatClient(clientData.getServerName(), clientData.getServerPort(), clientData.getUserName());
            loginEnable();
        } catch (Exception e) {
            quitConnection(QUIT_TYPE_NULL);
        }
    }

    private void sendMessageToServer(String Message) {
        try {
//            dataoutputstream.writeBytes(Message + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
            quitConnection(QUIT_TYPE_DEFAULT);
        }
    }

    private void loginEnable() {
        txtMessage.setEditable(true);
        btnSend.setDisable(false);
        rightPane.setDisable(false);
        logoutMenuItem.setDisable(false);
        loginMenuItem.setDisable(true);
    }

    private void disconnectChat() {
        System.out.println("here");
        quitConnection(QUIT_TYPE_DEFAULT);
    }

    private void quitConnection(int quitType) {
        logoutDisable();
    }

    private void logoutDisable() {
        txtMessage.setEditable(false);
        btnSend.setDisable(true);
        rightPane.setDisable(true);
        logoutMenuItem.setDisable(true);
        loginMenuItem.setDisable(false);

        userName = "";
        userRoom = "";
        totalUserCount = 0;
    }
}
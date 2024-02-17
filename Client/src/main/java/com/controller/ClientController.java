package com.controller;

import com.ClientApplication;
import com.client.ChatClient;
import com.client.Message;
import com.controller.tab.TabPaneManagerController;
import com.entity.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.common.CommonSettings.*;

@Component
//@Scope("prototype")
public class ClientController {
    private final ConfigurableApplicationContext springContext;
    private final User user;
    @Autowired
    private TabPaneManagerController tabPaneManagerController;
    @Autowired
    private TopController topController;
    @Autowired
    private CenterController centerController;
    @Autowired
    private BottomController bottomController;
    @FXML
    private  BorderPane topPane;
    @FXML
    private BorderPane rightPane;
    @FXML
    private ScrollPane centerPane;
    @FXML
    private BorderPane bottomPane;
    private Message message;
    private ChatClient chatClient;

    @Autowired
    public ClientController(ConfigurableApplicationContext springContext, User user) {
        this.springContext = springContext;
        this.user = user;
    }

    @FXML
    private void initialize() {
        message = new Message(new Label());
    }

    //Instance Method
    public void openLoginWindow() throws IOException {
        showLoginStage();
        centerController.getMessageBoard().getChildren().clear();
        display("Connecting To Server... Please Wait...\n", MESSAGE_TYPE_ADMIN);
    }

    @FXML
    public void showLoginStage() throws IOException {
        var loader = new FXMLLoader(ClientApplication.class.getResource("/com/controller/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage loginStage = new Stage();
        loginStage.getIcons().add(new Image(ClientApplication.class.getResource("/images/icon.gif").toString()));
        loginStage.setTitle(PRODUCT_NAME + " - Login");
        loginStage.setAlwaysOnTop(true);
        loginStage.setResizable(false);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setScene(new Scene(root, 250, 400));
        loginStage.show();
    }

    void openSignupWindow() throws IOException {
        showSignupStage();
        centerController.getMessageBoard().getChildren().clear();
        display("Connecting To Server... Please Wait...\n", MESSAGE_TYPE_ADMIN);
    }

    @FXML
    public void showSignupStage() throws IOException {
        var loader = new FXMLLoader(ClientApplication.class.getResource("/com/controller/signup.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage signupStage = new Stage();
        signupStage.getIcons().add(new Image(ClientApplication.class.getResource("/images/icon.gif").toString()));
        signupStage.setTitle(PRODUCT_NAME + " - Sign Up");
        signupStage.setAlwaysOnTop(true);
        signupStage.setResizable(false);
        signupStage.initModality(Modality.APPLICATION_MODAL);
        signupStage.setScene(new Scene(root, 250, 400));
        signupStage.show();
    }

    // Function To Send MESS Rfc to Server

    public void connectToServer(String code) throws IOException {
        chatClient = createClient();
        chatClient.startConnection(user.isProxyState(), code);
        enableLogin();
    }

    //Function To Update the Information Label
    public void display(String message, Integer type) {
        List<Node> nodes = this.message.parseMessage(message, type);
        centerController.getMessageBoard().getChildren().addAll(nodes);
    }

    //Function To Send Private Message To Server
    public void sentPrivateMessageToServer(String message, String toUserName) throws IOException {
        chatClient.sendMessageToServer("PRIV " + toUserName + " " + user.getUserName() + " " + message);
    }

    // Function to Send an RFC for Get a Room User Count
    public void getRoomUserCount(String RoomName) throws IOException {
        chatClient.sendMessageToServer("ROCO " + RoomName);
    }

    public void handleUserException(String[] tokensMsg) {
        quitConnection(QUIT_TYPE_NULL);
    }

    // EXCP user not found in the database!
    public void handleException(String[] tokens) {
        String message = tokens[1];
        display("Server: " + message, MESSAGE_TYPE_ADMIN);

        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
        quitConnection(QUIT_TYPE_NULL);
    }

    // KICK
    public void handleKickUser() {
        display("You are Kicked Out From Chat for flooding the message!", MESSAGE_TYPE_ADMIN);
        quitConnection(QUIT_TYPE_KICK);
    }

    public void changeRoom(String userName, String selectedRoom) throws IOException {
        chatClient.sendMessageToServer("CHRO " + user.getUserName() + " " + selectedRoom);
    }

    public void shutdown() {
        quitConnection(QUIT_TYPE_DEFAULT);
        Platform.exit();
    }

    void quitConnection(int quitType) {
        if (chatClient != null)
            chatClient.closeConnection(quitType);

        disableLogout();
        topController.informationLabel.setText("Information Label");
        display("ADMIN: CONNECTION TO THE SERVER IS CLOSED.", MESSAGE_TYPE_ADMIN);
    }

    private void enableLogin() {
        control(true);
    }

    private void disableLogout() {
        control(false);
        user.setUserName("");
        user.setRoomName("");
        user.getClients().clear();
        tabPaneManagerController.getUsersTabController().getUserView().getItems().clear();
    }

    private void control(boolean status) {
        bottomController.getTxtMessage().setEditable(status);
        bottomController.btnSend.setDisable(!status);
        rightPane.setDisable(!status);
        topController.loginMenuItem.setDisable(status);
        topController.signupMenuItem.setDisable(status);
        topController.logoutMenuItem.setDisable(!status);
    }

    private ChatClient createClient() {
        return new ChatClient(
                this,
                tabPaneManagerController,
                user,
                (data, type) ->
                        Platform.runLater(() -> { //UI or background thread - manipulate a UI object, It gives control back to UI thread
                            display(data.toString(), type);
                        }));
    }


    //Bean Methods
    public User getUser() {
        return user;
    }


    public void sendMessage(String text) throws IOException {
        chatClient.sendMessageToServer("MESS " + user.getRoomName() + " " + user.getUserName() + " " + text);
        display(user.getUserName() + ": " + text, MESSAGE_TYPE_DEFAULT);
    }
}
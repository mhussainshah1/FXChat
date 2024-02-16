package com.controller;

import com.ClientApplication;
import com.client.ChatClient;
import com.client.Message;
import com.entity.Client;
import com.entity.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.common.CommonSettings.*;

@Component
//@Scope("prototype")
public class ClientController {
    private final ConfigurableApplicationContext springContext;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private BorderPane rightPane;
    private Message message;
    @FXML
    private Button btnChangeRoom;
    @FXML
    private Button btnIgnoreUser;
    @FXML
    private TextField txtUserCount;
    @FXML
    private Label informationLabel;
    @FXML
    private ListView<Label> userView;
    @FXML
    private ListView<Label> roomView;
    @FXML
    private TilePane tilePane;
    @FXML
    private TextFlow messageBoard;
    @FXML
    private Button btnSend;
    @FXML
    private TextField txtMessage;
    @FXML
    private MenuItem loginMenuItem;
    @FXML
    private MenuItem signupMenuItem;
    @FXML
    private MenuItem logoutMenuItem;
    private ArrayList<PrivateChatController> privateWindows;
    private ChatClient chatClient;
    private String selectedUser, selectedRoom;
    private User user;

    @Autowired
    public ClientController(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    @FXML
    private void initialize() {
        selectedUser = "";
        message = new Message(new Label());
        privateWindows = new ArrayList<>();
        messageBoard.heightProperty().addListener((observable, oldValue, newValue) -> sp_main.setVvalue((Double) newValue));
    }

    //Event Handlers
    @FXML
    private void menuHandler(ActionEvent e) throws IOException {
        var alert = new Alert(Alert.AlertType.NONE);
        var name = ((MenuItem) e.getTarget()).getText();

        switch (name) {
            case "Login" -> openLoginWindow();
            case "Signup" -> openSignupWindow();
            case "Logout" -> quitConnection(QUIT_TYPE_DEFAULT);
            case "Exit" -> shutdown();
            case "About" -> {
                alert.setTitle("About Us");
                alert.setHeaderText(PRODUCT_NAME);
                alert.setContentText("\nDeveloped By...\n" + COMPANY_NAME);
                alert.setGraphic(new ImageView(getClass().getResource("/icons/photo13.gif").toString()));
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.show();
            }
        }
    }

    @FXML
    private void btnHandler(ActionEvent e) throws IOException {
        var button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Send Message!")) {
            if (!txtMessage.getText().isEmpty()) {
                sendMessage();
            }
        } else if (name.equals("Exit Chat")) {
            shutdown();
        } else if (name.equals("Change Room")) {
            //Change Room Coding
            changeRoom();
        } else if (e.getSource().equals(btnIgnoreUser)) {
            ignoreUser(name.equals("Ignore User"));
        } else if (name.equals("Send Direct Message")) {
            sendDirectMessage();
        }
    }

    @FXML
    private void txtHandler(ActionEvent e) {
        btnSend.fire();
    }

    @FXML
    private void iconHandler(MouseEvent mouseEvent) {
        Label icon = (Label) mouseEvent.getSource();

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
            icon.setStyle("-fx-border-color: black");

        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            txtMessage.appendText("~~" + icon.getText() + " ");

        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
            icon.setStyle("-fx-border-color: white");
        }
    }

    @FXML
    private void listViewHandler(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            btnIgnoreUser.setText("Ignore User");
            if (mouseEvent.getSource().equals(userView)) {
                var userSelModel = userView.getSelectionModel();
                ObservableList<Label> selectedItems = userSelModel.getSelectedItems();
                Label label = selectedItems.get(0);
                selectedUser = label.getText();

                if (isIgnoredUser(selectedUser)) {
                    btnIgnoreUser.setText("Allow User");
                } else {
                    btnIgnoreUser.setText("Ignore User");
                }

                if ((mouseEvent.getClickCount() == 2) /*&& (!(selectedUser.isEmpty()))*/ && (!(selectedUser.equals(user.getUserName())))) {
                    handlePrivateChat(new String[]{null, selectedUser, null});
                }
            }

            txtUserCount.setText("");
            if (mouseEvent.getSource().equals(roomView)) {
                MultipleSelectionModel<Label> selectionModel = roomView.getSelectionModel();
                ObservableList<Label> selectedItems = selectionModel.getSelectedItems();
                Label label = selectedItems.get(0);
                selectedRoom = label.getText();
                try {
                    getRoomUserCount(selectedRoom);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    //Instance Method

    public void openLoginWindow() throws IOException {
        showLoginStage();
        messageBoard.getChildren().clear();
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

    private void openSignupWindow() throws IOException {
        showSignupStage();
        messageBoard.getChildren().clear();
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

    public void connectToServer(String code) throws IOException {
        chatClient = createClient();
        chatClient.startConnection(user.isProxyState(), code);
        enableLogin();
    }

    // Function To Send MESS Rfc to Server

    private void sendMessage() throws IOException {
        chatClient.sendMessageToServer("MESS " + user.getRoomName() + " " + user.getUserName() + " " + txtMessage.getText());
        display(user.getUserName() + ": " + txtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        txtMessage.clear();
        txtMessage.requestFocus();
    }
    // To send a message to the text flow

    public void display(String message, Integer type) {
        List<Node> nodes = this.message.parseMessage(message, type);
        messageBoard.getChildren().addAll(nodes);
    }
    //Function To Update the Information Label

    //Function To Update the Information Label
    private void updateInformationLabel() {
        String builder = "User Name: " + user.getUserName() + "\t\t" +
                "Room Name: " + user.getRoomName() + "\t\t" +
                "No. Of Users: " + user.getClients().size() + "\t\t";
        informationLabel.setText(builder);

        try {
            getRoomUserCount(user.getRoomName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("ChatClientController - users [");
        for (Client client : user.getClients()) {
            System.out.print(client.getClientName() + " ");
        }
        System.out.println("]");
    }

    // LIST ali amir
    public void handleList(String[] tokens) {
        //Add Label into ListView for Users
        userView.getItems().clear();
        user.getClients().clear();
        for (int j = 1; j < tokens.length; j++) {
            Label label = new Label(tokens[j], new ImageView(getClass().getResource("/icons/photo11.gif").toString()));
            addClient(userView, label);
        }
        selectedRoom = user.getRoomName();

        //Update the Information Label
        updateInformationLabel();

        messageBoard.getChildren().clear();
        display("Welcome To The " + user.getRoomName() + " Room!", MESSAGE_TYPE_JOIN);
    }

    // ROOM General Teen Music Party
    public void handleRoom(String[] tokens) {
        //Add User Item into User Canvas
        roomView.getItems().clear();
//        user.getRoomList().clear();
        // Loading Room List in to Room Canvas
        for (int j = 1; j < tokens.length; j++) {
            Label label = new Label(tokens[j], new ImageView(getClass().getResource("/icons/photo13.gif").toString()));
            addRoom(roomView, label);
        }
    }

    // ADD amir
    public void handleLogin(String[] tokens) {
        //Add User Item into User Canvas
        String tokenUserName = tokens[1];
        enablePrivateWindow(tokenUserName);
        Label label = new Label(tokenUserName, new ImageView(getClass().getResource("/icons/photo11.gif").toString()));
        addClient(userView, label);

        //Update the Information Label
        updateInformationLabel();

        display(tokenUserName + " joins chat...", MESSAGE_TYPE_JOIN);
    }

    public void handleUserException(String[] tokensMsg) {
        quitConnection(QUIT_TYPE_NULL);
    }

    // REMO ali
    public void handleLoggOff(String[] tokens) {
        String tokenUserName = tokens[1];

        removeClient(userView, tokenUserName);
        removeUserFromPrivateChat(tokenUserName);

        //Update the Information Label
        updateInformationLabel();

        display(tokenUserName + " has been logged out from chat!", MESSAGE_TYPE_LEAVE);
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

    // MESS amir <Hi all>
    public void handleMessage(String[] tokens) {
        String userName = tokens[1];
        String message = tokens[2];

        // Check whether ignored user
        if (userName.equalsIgnoreCase("Server")) {
            display(userName + ": " + message, MESSAGE_TYPE_ADMIN);
        } else if (!(isIgnoredUser(userName)))
            display(userName + ": " + message, MESSAGE_TYPE_DEFAULT);
    }

    // KICK
    public void handleKickUser() {
        display("You are Kicked Out From Chat for flooding the message!", MESSAGE_TYPE_ADMIN);
        quitConnection(QUIT_TYPE_KICK);
    }

    // INKI ali
    public void handleKickUserInfo(String[] tokens) {
        String tokenUserName = tokens[1];
        removeClient(userView, tokenUserName);
        removeUserFromPrivateChat(tokenUserName);

        //Update the Information Label
        updateInformationLabel();

        display(tokenUserName + " has been kicked Out from Chat by the Administrator!", MESSAGE_TYPE_ADMIN);
    }

    // CHRO Teen
    public void handleChangeRoom(String userRoom) {
        user.setRoomName(userRoom);
    }

    // JORO ali
    public void handleJoinRoom(String[] tokens) {
        String tokenUserName = tokens[1];
        Label label = new Label(tokenUserName, new ImageView(getClass().getResource("/icons/photo11.gif").toString()));
        addClient(userView, label);

        //Update the Information Label
        updateInformationLabel();

        display(tokenUserName + " joins chat...", MESSAGE_TYPE_JOIN);
    }

    // LERO amir Teen
    public void handleLeaveRoom(String[] tokens) {
        String tokenUserName = tokens[1];
        String tokenRoomName = tokens[2];
        removeClient(userView, tokenUserName);

        //Update the Information Label
        updateInformationLabel();

        display(tokenUserName + " has left " + user.getRoomName() + " Room and joined into " + tokenRoomName + " Room", MESSAGE_TYPE_ADMIN);
    }

    // ROCO General 2
    public void handleRoomCount(String[] tokens) {
        String tokenRoomName = tokens[1];
        txtUserCount.setText("Total Users in " + tokenRoomName + " : " + tokens[2]);
    }

    // PRIV ali hi
    public void handlePrivateChat(String[] tokens) {
        String userName = tokens[1];
        String message = tokens[2];

        //Check whether ignored user
        if (!(isIgnoredUser(userName))) {
            boolean privateFlag = false;

            PrivateChatController privateWindow = getPrivateWindowByUserName(userName);
            if (privateWindow != null) {
                if (message != null)
                    privateWindow.display(userName + ": " + message, MESSAGE_TYPE_DEFAULT);
                privateWindow.getStage().show();
                privateWindow.getStage().requestFocus();
                privateFlag = true;
            }

            if (!(privateFlag)) {
                if (privateWindows.size() >= MAX_PRIVATE_WINDOW) {
                    display("You are exceeding private window limit! So you may lose some message from your friends!", MESSAGE_TYPE_ADMIN);
                } else {
                    try {
                        privateWindow = showPrivateChatStage(userName);
                        if (message != null)
                            privateWindow.display(userName + ": " + message, MESSAGE_TYPE_DEFAULT);
                        privateWindow.getStage().show();
                        privateWindow.getStage().requestFocus();
                        addPrivateWindow(privateWindow);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Function To add the Given Item From the List Array
    public void addClient(ListView<Label> listView, Label label) {
        boolean found = false;
        for (Client client : user.getClients()) {
            if (client.getClientName().equalsIgnoreCase(label.getText())) {
                found = true;
                break;
            }
        }
        if (!found) {
            user.getClients().add(new Client(label.getText()));
        }
        listView.getItems().add(label);
    }

    public void addRoom(ListView<Label> listView, Label label) {
        user.getRoomList().add(label.getText());
        listView.getItems().add(label);
    }

    // Function To Remove the Given Item From the List Array
    private void removeClient(ListView<Label> items, String clientName) {
        for (Label label : items.getItems()) {
            if (label.getText().equalsIgnoreCase(clientName)) {
                items.getItems().remove(label);
                user.getClients().remove(getClientByClientName(clientName));
                break;
            }
        }
    }

    //List ViewCanvas Methods in Jeeva Project
    protected void ignoreUser(boolean isIgnore) {
        if (selectedUser.isEmpty()) {
            display("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedUser.equals(user.getUserName())) {
            display("You can not ignored yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }
        ignoreUser(isIgnore, selectedUser);
    }

    //Set or Remove Ignore List from Array
    protected void ignoreUser(boolean isIgnore, String ignoreUserName) {
        PrivateChatController privateWindow = getPrivateWindowByUserName(ignoreUserName);
        if (privateWindow != null) {
            privateWindow.handleBtnIgnoreUser(btnIgnoreUser.getText());
        }

        Client client = getClientByClientName(ignoreUserName);
        if (client != null) {
            client.setIgnored(isIgnore);
            if (isIgnore) {
                btnIgnoreUser.setText("Allow User");
                display(ignoreUserName + " has been ignored!", MESSAGE_TYPE_LEAVE);
            } else {
                btnIgnoreUser.setText("Ignore User");
                display(ignoreUserName + " has been removed from ignored list!", MESSAGE_TYPE_JOIN);
            }
        }
    }

    //Function To Get the Index of Give Message from List Array
    public Client getClientByClientName(String clientName) {
        for (Client client : user.getClients()) {
            //Label client = message.getLabel();
            if (client.getClientName().equalsIgnoreCase(clientName)) {
                return client;
            }
        }
        return null;
    }

    public boolean isIgnoredUser(String userName) {
        Client client = getClientByClientName(userName);
        return client.isIgnored();
    }

    @FXML
    public PrivateChatController showPrivateChatStage(String selectedUser) throws IOException {
        var loader = new FXMLLoader(ClientApplication.class.getResource("/com/controller/privatechat.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Stage privateChatStage = new Stage();
        privateChatStage.getIcons().add(new Image(ClientApplication.class.getResource("/images/icon.gif").toString()));
        privateChatStage.setTitle("Private Chat with - " + selectedUser);
        privateChatStage.setHeight(PRIVATE_WINDOW_HEIGHT);
        privateChatStage.setWidth(PRIVATE_WINDOW_WIDTH);
        privateChatStage.setScene(new Scene(root));
        privateChatStage.setResizable(false);
//        privateChatStage.setOnHidden(e->privateChatController.exitPrivateWindow());

        PrivateChatController privateChatController = loader.getController();
//        privateChatController.setClientController(this);
        privateChatController.setUserName(selectedUser);
        return privateChatController;
    }

    //Enable the Private Chat when the End User logged out
    public PrivateChatController getPrivateWindowByUserName(String userName) {
        for (PrivateChatController privateWindow : privateWindows) {
            if (privateWindow.getUserName().equalsIgnoreCase(userName)) {
                return privateWindow;
            }
        }
        return null;
    }

    private void enablePrivateWindow(String toUserName) {
        PrivateChatController privateWindow = getPrivateWindowByUserName(toUserName);
        if (privateWindow != null) {
            privateWindow.display(toUserName + " is Currently Online!", MESSAGE_TYPE_ADMIN);
            privateWindow.enableAll();
        }
    }

    //Disable the Private Chat when the End User logged out
    private void removeUserFromPrivateChat(String toUserName) {
        PrivateChatController privateWindow = getPrivateWindowByUserName(toUserName);
        if (privateWindow != null) {
            privateWindow.display(toUserName + " is Currently Offline!", MESSAGE_TYPE_ADMIN);
            privateWindow.disableAll();
        }
    }
    //Function To Send Private Message To Server

    public void sentPrivateMessageToServer(String message, String toUserName) throws IOException {
        chatClient.sendMessageToServer("PRIV " + toUserName + " " + user.getUserName() + " " + message);
    }
    private void addPrivateWindow(PrivateChatController privateWindow) {
        privateWindows.add(privateWindow);
    }

    // Function To Remove Private Window
    protected void removePrivateWindow(String toUserName) {
        PrivateChatController privateWindow = getPrivateWindowByUserName(toUserName);
        if (privateWindow != null) {
            privateWindows.remove(privateWindow);
            privateWindows.trimToSize();
        }
    }

    // Function to Change Room
    protected void changeRoom() throws IOException {
        if (selectedRoom.equals("")) {
            display("Invalid Room Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedRoom.equals(user.getRoomName())) {
            display("You are already in that ROOM!", MESSAGE_TYPE_ADMIN);
            return;
        }
        chatClient.sendMessageToServer("CHRO " + user.getUserName() + " " + selectedRoom);
        getRoomUserCount(selectedRoom);
    }

    // Function to Send an RFC for Get a Room User Count

    protected void getRoomUserCount(String RoomName) throws IOException {
        chatClient.sendMessageToServer("ROCO " + RoomName);
    }

    public void shutdown() {
        quitConnection(QUIT_TYPE_DEFAULT);
        Platform.exit();
    }

    private void quitConnection(int quitType) {
        if (chatClient != null)
            chatClient.closeConnection(quitType);

        disableLogout();
        informationLabel.setText("Information Label");
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
        userView.getItems().clear();
    }

    private void control(boolean status) {
        txtMessage.setEditable(status);
        btnSend.setDisable(!status);
        rightPane.setDisable(!status);
        loginMenuItem.setDisable(status);
        signupMenuItem.setDisable(status);
        logoutMenuItem.setDisable(!status);
    }

    private ChatClient createClient() {
        return new ChatClient(
                this,
                user,
                (data, type) ->
                        Platform.runLater(() -> { //UI or background thread - manipulate a UI object, It gives control back to UI thread
                            display(data.toString(), type);
                        }));
    }

    protected void sendDirectMessage() {
        if (selectedUser.isEmpty()) {
            display("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedUser.equals(user.getUserName())) {
            display("You can not chat with yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }
        handlePrivateChat(new String[]{null, selectedUser, null});
    }
    //Bean Methods

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
package com.controller;

import com.ClientApplication;
import com.client.ChatClient;
import com.common.Message;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.common.CommonSettings.*;


public class ClientController {
    public ScrollPane sp_main;
    @FXML
    private Button btnChangeRoom;
    @FXML
    private Button btnIgnoreUser;
    @FXML
    private TextField txtUserCount;
    @FXML
    private Label informationLabel;
    @FXML
    private GridPane login;
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
    private TabPane rightPane;
    @FXML
    private MenuItem loginMenuItem;
    @FXML
    private MenuItem signupMenuItem;
    @FXML
    private MenuItem logoutMenuItem;
    private ArrayList<PrivateChatController> privateWindows;
    private String userName;
    private String userRoom;
    private ArrayList<Message> messages;
    private int totalUserCount;
    private ChatClient chatClient;
    private LoginController loginController;
    private Message messageObject;
    private String serverName;
    private int serverPort;
    private boolean proxy;
    private String proxyHost;
    private int proxyPort;
    private String selectedUser, selectedRoom;

    @FXML
    private void initialize() {
        messages = new ArrayList<>();
        selectedUser = "";
        messageObject = new Message(new Label());
        privateWindows = new ArrayList<>();
        messageBoard.heightProperty().addListener((observable, oldValue, newValue) -> sp_main.setVvalue((Double) newValue));
    }

    //Event Handlers
    @FXML
    private void menuHandler(ActionEvent e) throws IOException {
        var alert = new Alert(Alert.AlertType.NONE);
        var name = ((MenuItem) e.getTarget()).getText();

        if (name.equals("Login")) {
            openLoginWindow();
        } else if (name.equals("Signup")) {
            openSignupWindow();
        } else if (name.equals("Logout")) {
            quitConnection(QUIT_TYPE_DEFAULT);
        } else if (name.equals("Exit")) {
            shutdown();
        } else if (name.equals("About")) {
            alert.setTitle("About Us");
            alert.setHeaderText(PRODUCT_NAME);
            alert.setContentText("\nDeveloped By...\n" + COMPANY_NAME);
            alert.setGraphic(new ImageView(getClass().getResource("/icons/photo13.gif").toString()));
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.show();
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

    public void shutdown() {
        quitConnection(QUIT_TYPE_DEFAULT);
        Platform.exit();
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
            txtUserCount.setText("");
            btnIgnoreUser.setText("Ignore User");
            if (mouseEvent.getSource().equals(userView)) {
                var userSelModel = userView.getSelectionModel();
                ObservableList<Label> selectedItems = userSelModel.getSelectedItems();
                Label label = selectedItems.get(0);
                selectedUser = label.getText();

                if (isIgnoredUser(selectedUser))
                    btnIgnoreUser.setText("Allow User");
                else
                    btnIgnoreUser.setText("Ignore User");

                if ((mouseEvent.getClickCount() == 2) /*&& (!(selectedUser.equals("")))*/ && (!(selectedUser.equals(userName)))) {
                    handlePrivateChat(new String[]{null, selectedUser, null});
                }
            }

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
        ClientApplication.showLoginStage();
        messageBoard.getChildren().clear();
        display("Connecting To Server... Please Wait...\n", MESSAGE_TYPE_ADMIN);
    }
    private void openSignupWindow() {

    }

    public void loginToChat() {
        if (loginController.isConnect()) {
            userName = loginController.getUserName();
            userRoom = loginController.getUserRoom();
            serverName = loginController.getServerName();
            serverPort = loginController.getServerPort();
            if (loginController.isProxyCheckBox()) {
                proxy = true;
                proxyHost = loginController.getProxyHost();
                proxyPort = loginController.getProxyPort();
            } else {
                proxy = false;
            }
        }
        try {
            connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
            display(e.toString(), MESSAGE_TYPE_ADMIN);
        }
    }

    private void connectToServer() throws IOException {
        chatClient = createClient();
        chatClient.startConnection(proxy);
        enableLogin();
    }

    // Function To Send MESS Rfc to Server
    private void sendMessage() throws IOException {
        chatClient.sendMessageToServer("MESS " + userRoom + " " + userName + " " + txtMessage.getText());
        display(userName + ": " + txtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    // To send a message to the text flow
    private void display(String message, Integer type) {
        List<Node> nodes = messageObject.parseMessage(message, type);
        messageBoard.getChildren().addAll(nodes);
    }

    //Function To Update the Information Label
    private void updateInformationLabel() {
        String builder = "User Name: " + userName + "       " +
                "Room Name: " + userRoom + "       " +
                "No. Of Users: " + totalUserCount + "       ";
        informationLabel.setText(builder);
    }

    // LIST ali amir
    public void handleList(String[] tokens) {
        totalUserCount = tokens.length - 1;
        //Update the Information Label
        updateInformationLabel();
        //Add Label into ListView for Users
        userView.getItems().clear();
        for (int j = 1; j < tokens.length; j++) {
            Label label = new Label(tokens[j], new ImageView(getClass().getResource("/icons/photo11.gif").toString()));
            addListItem(userView, label);
        }
        messageBoard.getChildren().clear();
        display("Welcome To The " + userRoom + " Room!", MESSAGE_TYPE_JOIN);
    }

    // ROOM General Teen Music Party
    public void handleRoom(String[] tokens) {
        //userRoom = tokens[1];
        updateInformationLabel();
        //Add User Item into User Canvas
        roomView.getItems().clear();
        // Loading Room List in to Room Canvas
        for (int j = 1; j < tokens.length; j++) {
            Label label = new Label(tokens[j], new ImageView(getClass().getResource("/icons/photo13.gif").toString()));
            addListItem(roomView, label);
        }
    }

    // ADD amir
    public void handleLogin(String[] tokens) {
        //Update the Information Label
        totalUserCount++;
        updateInformationLabel();

        //Add User Item into User Canvas
        String tokenUserName = tokens[1];
        enablePrivateWindow(tokenUserName);
        Label label = new Label(tokenUserName, new ImageView(getClass().getResource("/icons/photo11.gif").toString()));
        addListItem(userView, label);
        display(tokenUserName + " joins chat...", MESSAGE_TYPE_JOIN);
    }

    // EXIS
    public void handleUserExist() {
        display("User name already exists... try again with another name!", MESSAGE_TYPE_ADMIN);
        quitConnection(QUIT_TYPE_NULL);
    }

    // REMO ali
    public void handleLoggOff(String[] tokens) {
        String tokenUserName = tokens[1];

        removeListItem(userView, tokenUserName);
        removeUserFromPrivateChat(tokenUserName);
        display(tokenUserName + " has been logged out from chat!", MESSAGE_TYPE_LEAVE);

        //Update the Information Label
        totalUserCount--;
        updateInformationLabel();
    }

    // MESS amir Hi all
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
        removeListItem(userView, tokenUserName);
        removeUserFromPrivateChat(tokenUserName);
        display(tokenUserName + " has been kicked Out from Chat by the Administrator!", MESSAGE_TYPE_ADMIN);

        //Update the Information Label
        totalUserCount--;
        updateInformationLabel();
    }

    // CHRO Teen
    public void handleChangeRoom(String userRoom) {
        this.userRoom = userRoom;
    }

    // JORO ali
    public void handleJoinRoom(String[] tokens) {
        String tokenUserName = tokens[1];
        Label label = new Label(tokenUserName, new ImageView(getClass().getResource("/icons/photo11.gif").toString()));
        addListItem(userView, label);
        //Update the Information Label
        totalUserCount++;
        updateInformationLabel();

        display(tokenUserName + " joins chat...", MESSAGE_TYPE_JOIN);
    }

    // LERO amir Teen
    public void handleLeaveRoom(String[] tokens) {
        String tokenUserName = tokens[1];
        String tokenRoomName = tokens[2];
        removeListItem(userView, tokenUserName);
        display(tokenUserName + " has left " + userRoom + " Room and joined into " + tokenRoomName + " Room", MESSAGE_TYPE_ADMIN);

        //Update the Information Label
        totalUserCount--;
        updateInformationLabel();
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
                        privateWindow = ClientApplication.showPrivateChatStage(userName);
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

    public void addListItem(ListView<Label> listView, Label label) {
        messages.add(new Message(label));
        listView.getItems().add(label);
    }

    // Function To Remove the Given Item From the List Array

    private void removeListItem(ListView<Label> items, String text) {
        for (Label label : items.getItems()) {
            if (label.getText().equalsIgnoreCase(text)) {
                items.getItems().remove(label);
                messages.remove(getMessageByText(text));
                break;
            }
        }
    }
    //List ViewCanvas Methods in Jeeva Project

    protected void ignoreUser(boolean isIgnore) {
        if (selectedUser.equals("")) {
            display("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedUser.equals(getUserName())) {
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

        Message message = getMessageByText(ignoreUserName);
        if (message != null) {
            message.setIgnored(isIgnore);
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

    public Message getMessageByText(String text) {
        for (Message message : messages) {
            Label label = message.getLabel();
            if (label.getText().equalsIgnoreCase(text)) {
                return message;
            }
        }
        return null;
    }

    public boolean isIgnoredUser(String userName) {
        Message message = getMessageByText(userName);
        return message.isIgnored();
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
        chatClient.sendMessageToServer("PRIV " + toUserName + " " + userName + " " + message);
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

        if (selectedRoom.equals(userRoom)) {
            display("You are already in that ROOM!", MESSAGE_TYPE_ADMIN);
            return;
        }
        chatClient.sendMessageToServer("CHRO " + userName + " " + selectedRoom);
    }

    // Function to Send an RFC for Get a Room User Count
    protected void getRoomUserCount(String RoomName) throws IOException {
        chatClient.sendMessageToServer("ROCO " + RoomName);
    }

    private void quitConnection(int quitType) {
        if (chatClient != null)
            chatClient.closeConnection(quitType);

        disableLogout();
        display("ADMIN: CONNECTION TO THE SERVER IS CLOSED.", MESSAGE_TYPE_ADMIN);
    }

    private void enableLogin() {
        control(true);
    }

    private void disableLogout() {
        control(false);
        userName = "";
        userRoom = "";
        totalUserCount = 0;
        userView.getItems().clear();
    }

    private void control(boolean status) {
        txtMessage.setEditable(status);
        btnSend.setDisable(!status);
        rightPane.setDisable(!status);
        logoutMenuItem.setDisable(!status);
        loginMenuItem.setDisable(status);
    }

    private ChatClient createClient() {
        return new ChatClient(this, userName, userRoom, serverName, serverPort, proxyHost, proxyPort, (data, type) ->
                Platform.runLater(() -> { //UI or background thread - manipulate UI object , It gives control back to UI thread
                    display(data.toString(), type);
                }));
    }

    //Bean Methods
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public String getUserName() {
        return userName;
    }

    protected void sendDirectMessage() {
        if (selectedUser.equals("")) {
            display("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedUser.equals(getUserName())) {
            display("You can not chat with yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }
        handlePrivateChat(new String[]{null, selectedUser, null});
    }

    private void addPrivateWindow(PrivateChatController privateWindow) {
        privateWindows.add(privateWindow);
    }

}
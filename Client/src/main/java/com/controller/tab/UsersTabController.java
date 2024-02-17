package com.controller.tab;

import com.ClientApplication;
import com.controller.PrivateChatController;
import com.controller.TopController;
import com.entity.Client;
import com.entity.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

import static com.common.CommonSettings.*;

@Component
public class UsersTabController {
    private final ConfigurableApplicationContext springContext;
    private final TabPaneManagerController tabPaneManagerController;
    private final TopController topController;
    private final User user;
    @FXML
    private Button btnIgnoreUser;
    @FXML
    private ListView<Label> userView;
    private String selectedUser;
    private ArrayList<PrivateChatController> privateWindows;

    @Autowired
    public UsersTabController(ConfigurableApplicationContext springContext, TabPaneManagerController tabPaneManagerController, TopController topController, User user) {
        this.springContext = springContext;
        this.tabPaneManagerController = tabPaneManagerController;
        this.topController = topController;
        this.user = user;
    }

    @FXML
    private void initialize() {
        selectedUser = "";
        privateWindows = new ArrayList<>();
    }

    @FXML
    private void listViewHandler(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            btnIgnoreUser.setText("Ignore User");
            if (mouseEvent.getSource().equals(userView)) {
                var userSelModel = userView.getSelectionModel();
                ObservableList<Label> selectedItems = userSelModel.getSelectedItems();
                Label label = selectedItems.getFirst();
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
        }
    }

    @FXML
    private void btnHandler(ActionEvent e) {
        var button = (Button) e.getTarget();
        var name = button.getText();
        if (e.getSource().equals(btnIgnoreUser)) {
            ignoreUser(name.equals("Ignore User"));
        } else if (name.equals("Send Direct Message")) {
            sendDirectMessage();
        }
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

        //Update the Information Label
        topController.updateInformationLabel();

        tabPaneManagerController.getMessageBoard().getChildren().clear();
        tabPaneManagerController.display("Welcome To The " + user.getRoomName() + " Room!", MESSAGE_TYPE_JOIN);
    }

    // ADD amir
    public void handleLogin(String[] tokens) {
        //Add User Item into User Canvas
        String tokenUserName = tokens[1];
        enablePrivateWindow(tokenUserName);
        Label label = new Label(tokenUserName, new ImageView(getClass().getResource("/icons/photo11.gif").toString()));
        addClient(userView, label);

        //Update the Information Label
        topController.updateInformationLabel();

        tabPaneManagerController.display(tokenUserName + " joins chat...", MESSAGE_TYPE_JOIN);
    }

    // REMO ali
    public void handleLoggOff(String[] tokens) {
        String tokenUserName = tokens[1];

        removeClient(userView, tokenUserName);
        removeUserFromPrivateChat(tokenUserName);

        //Update the Information Label
        topController.updateInformationLabel();

        tabPaneManagerController.display(tokenUserName + " has been logged out from chat!", MESSAGE_TYPE_LEAVE);
    }
    // MESS amir <Hi all>

    public void handleMessage(String[] tokens) {
        String userName = tokens[1];
        String message = tokens[2];

        // Check whether ignored user
        if (userName.equalsIgnoreCase("Server")) {
            tabPaneManagerController.display(userName + ": " + message, MESSAGE_TYPE_ADMIN);
        } else if (!(isIgnoredUser(userName)))
            tabPaneManagerController.display(userName + ": " + message, MESSAGE_TYPE_DEFAULT);
    }

    // INKI ali

    public void handleKickUserInfo(String[] tokens) {
        String tokenUserName = tokens[1];
        removeClient(userView, tokenUserName);
        removeUserFromPrivateChat(tokenUserName);

        //Update the Information Label
        topController.updateInformationLabel();

        tabPaneManagerController.display(tokenUserName + " has been kicked Out from Chat by the Administrator!", MESSAGE_TYPE_ADMIN);
    }
    // JORO ali

    public void handleJoinRoom(String[] tokens) {
        String tokenUserName = tokens[1];
        Label label = new Label(tokenUserName, new ImageView(getClass().getResource("/icons/photo11.gif").toString()));
        addClient(userView, label);

        //Update the Information Label
        topController.updateInformationLabel();

        tabPaneManagerController.display(tokenUserName + " joins chat...", MESSAGE_TYPE_JOIN);
    }
    // LERO amir Teen

    public void handleLeaveRoom(String[] tokens) {
        String tokenUserName = tokens[1];
        String tokenRoomName = tokens[2];
        removeClient(userView, tokenUserName);

        //Update the Information Label
        topController.updateInformationLabel();

        tabPaneManagerController.display(tokenUserName + " has left " + user.getRoomName() + " Room and joined into " + tokenRoomName + " Room", MESSAGE_TYPE_ADMIN);
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
                    tabPaneManagerController.display("You are exceeding private window limit! So you may lose some message from your friends!", MESSAGE_TYPE_ADMIN);
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
    // Function To add the Given Item in the ArrayList

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
    // Function To Remove the Given Item From the ArrayList

    private void removeClient(ListView<Label> items, String clientName) {
        for (Label label : items.getItems()) {
            if (label.getText().equalsIgnoreCase(clientName)) {
                items.getItems().remove(label);
                user.getClients().remove(getClientByClientName(clientName));
                break;
            }
        }
    }

    protected void ignoreUser(boolean isIgnore) {
        if (selectedUser.isEmpty()) {
            tabPaneManagerController.display("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedUser.equals(user.getUserName())) {
            tabPaneManagerController.display("You can not ignored yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }
        ignoreUser(isIgnore, selectedUser);
    }

    //Set or Remove Ignore List from Array
    public void ignoreUser(boolean isIgnore, String ignoreUserName) {
        PrivateChatController privateWindow = getPrivateWindowByUserName(ignoreUserName);
        if (privateWindow != null) {
            privateWindow.handleBtnIgnoreUser(btnIgnoreUser.getText());
        }

        Client client = getClientByClientName(ignoreUserName);
        if (client != null) {
            client.setIgnored(isIgnore);
            if (isIgnore) {
                btnIgnoreUser.setText("Allow User");
                tabPaneManagerController.display(ignoreUserName + " has been ignored!", MESSAGE_TYPE_LEAVE);
            } else {
                btnIgnoreUser.setText("Ignore User");
                tabPaneManagerController.display(ignoreUserName + " has been removed from ignored list!", MESSAGE_TYPE_JOIN);
            }
        }
    }
    //Function To Get the Client from ArrayList

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

    protected void sendDirectMessage() {
        if (selectedUser.isEmpty()) {
            tabPaneManagerController.display("Invalid User Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedUser.equals(user.getUserName())) {
            tabPaneManagerController.display("You can not chat with yourself!", MESSAGE_TYPE_ADMIN);
            return;
        }
        handlePrivateChat(new String[]{null, selectedUser, null});
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

    private void addPrivateWindow(PrivateChatController privateWindow) {
        privateWindows.add(privateWindow);
    }

    // Function To Remove Private Window
    public void removePrivateWindow(String toUserName) {
        PrivateChatController privateWindow = getPrivateWindowByUserName(toUserName);
        if (privateWindow != null) {
            privateWindows.remove(privateWindow);
            privateWindows.trimToSize();
        }
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
        privateChatController.setUserName(selectedUser);
        return privateChatController;
    }

    public ListView<Label> getUserView() {
        return userView;
    }
}
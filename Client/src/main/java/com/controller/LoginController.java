package com.controller;

import com.common.CommonSettings;
import com.common.DBUtils;
import com.common.Data;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
public class LoginController {
    @FXML
    private ChoiceBox<String> choiceRoom;
    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtServerName;
    @FXML
    private TextField txtServerPort;
    @FXML
    private TextField txtProxyHost;
    @FXML
    private TextField txtProxyPort;
    @FXML
    private CheckBox proxyCheckBox;
    private boolean connect;
    private String roomName = "General";
    private ClientController clientController;

    //Calls automatically
    @FXML
    private void initialize() throws IOException {
        try (Data data = new Data("data.properties")) {
            txtUserName.setText(data.getUserName());
            txtPassword.setText(data.getPassword());
            choiceRoom.setItems(FXCollections.observableArrayList("General", "Teen", "Music", "Party"));
            choiceRoom.setOnAction(event -> {
                roomName = choiceRoom.getValue();
                choiceRoom.setValue(roomName);
            });

            txtServerName.setText(data.getServerName());
            txtServerPort.setText(String.valueOf(data.getServerPort()));
            proxyCheckBox.setSelected(data.isProxyState());
            txtProxyHost.setText(data.getProxyHost());
            txtProxyPort.setText(String.valueOf(data.getProxyPort()));
        }
    }

    //Handler
    @FXML
    private void actionHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Connect")) {
            try (var data = new Data("data.properties")) {
                data.setUserName(txtUserName.getText());
                data.setPassword(txtPassword.getText());
                data.setRoomName(roomName);
                data.setServerName(txtServerName.getText());
                data.setServerPort(Integer.parseInt(txtServerPort.getText()));
                data.setProxyState(proxyCheckBox.isSelected());
                data.setProxyHost(txtProxyHost.getText());
                data.setProxyPort(Integer.parseInt(txtProxyPort.getText()));
            }
            button.getScene().getWindow().hide();
            connect = true;
            loginToChat();
        } else if (name.equals("Quit")) {
            connect = false;
            button.getScene().getWindow().hide();
        } else if (name.equals("Signup")) {
            DBUtils.changeScene(e, "/com/controller/signup.fxml", CommonSettings.PRODUCT_NAME + " - Sign Up", null, null,clientController);
        }
    }

    public void loginToChat() {
        if (isConnect()) {
            clientController.setUserName (getUserName());
            clientController.setPassword(getPassword());
            clientController.setUserRoom(getUserRoom());
            clientController.setServerName(getServerName());
            clientController.setServerPort(getServerPort());
            if (isProxyCheckBox()) {
                clientController.setProxy(true);
                clientController.setProxyHost(getProxyHost());
                clientController.setProxyPort(getProxyPort());
            } else {
                clientController.setProxy(false);
            }
        }
        try {
            clientController.connectToServer("LOGN");
        } catch (IOException e) {
            e.printStackTrace();
            clientController.display(e.toString(), MESSAGE_TYPE_ADMIN);
        }
    }

    public boolean isConnect() {
        return connect;
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    public String getUserName() {
        return txtUserName.getText();
    }

    public String getPassword(){
        return txtPassword.getText();
    }

    public String getUserRoom() {
        return choiceRoom.getValue();
    }

    public String getServerName() {
        return txtServerName.getText();
    }

    public int getServerPort() {
        return Integer.parseInt(txtServerPort.getText());
    }

    public String getProxyHost() {
        return txtProxyHost.getText();
    }

    public int getProxyPort() {
        return Integer.parseInt(txtProxyPort.getText());
    }

    public boolean isProxyCheckBox() {
        return proxyCheckBox.isSelected();
    }
}
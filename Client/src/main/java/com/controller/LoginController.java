package com.controller;

import com.common.CommonSettings;
import com.entity.Data;
import com.entity.User;
import com.service.StageService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
@Scope("prototype")
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

    @Autowired
    private ClientController clientController;
    @Autowired
    private StageService stageService;

    //Calls automatically
    @FXML
    private void initialize() throws IOException {
        try (var data = new Data("data.properties")) {
//        var data = new User();
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
//            var data = new User();
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
            stageService.changeScene(e, "/com/controller/signup.fxml", CommonSettings.PRODUCT_NAME + " - Sign Up", null, null);
        }
    }

    public void loginToChat() {
        if (connect) {
            clientController.setUserName(txtUserName.getText());
            clientController.setPassword(txtPassword.getText());
            clientController.setUserRoom(choiceRoom.getValue());
            clientController.setServerName(txtServerName.getText());
            clientController.setServerPort(Integer.parseInt(txtServerPort.getText()));
            if (proxyCheckBox.isSelected()) {
                clientController.setProxy(true);
                clientController.setProxyHost(txtProxyHost.getText());
                clientController.setProxyPort(Integer.parseInt(txtProxyPort.getText()));
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
}
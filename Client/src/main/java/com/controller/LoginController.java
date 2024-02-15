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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    private User user;
    private String roomName = "General";

    @Autowired
    private ClientController clientController;
    @Autowired
    private StageService stageService;

    //Calls automatically
    @FXML
    private void initialize() throws IOException {
        choiceRoom.setItems(FXCollections.observableArrayList("General", "Teen", "Music", "Party"));
        choiceRoom.setOnAction(event -> {
            roomName = choiceRoom.getValue();
            choiceRoom.setValue(roomName);
        });
    }

    //Handler
    @FXML
    private void actionHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();
        if (name.equals("Connect")) {
            connect = true;
            loginToChat();
            button.getScene().getWindow().hide();
        } else if (name.equals("Quit")) {
            connect = false;
            button.getScene().getWindow().hide();
        } else if (name.equals("Signup")) {
            stageService.changeScene(e, "/com/controller/signup.fxml", CommonSettings.PRODUCT_NAME + " - Sign Up", null, null);
        }
    }

    public void loginToChat() {
        if (connect) {
            user = new User();
            user.setUserName(txtUserName.getText());
            user.setPassword(txtPassword.getText());
            user.setRoomName(choiceRoom.getValue());
            user.setServerName(txtServerName.getText());
            user.setServerPort(Integer.parseInt(txtServerPort.getText()));
            user.setMaximumGuestNumber(50);
            user.setRoomList(new HashSet<>(/*List.of("General", "Teen", "Music", "Party")*/));
            user.setProxyState(proxyCheckBox.isSelected());

            if (proxyCheckBox.isSelected()) {
                user.setProxyHost(txtProxyHost.getText());
                user.setProxyPort(Integer.parseInt(txtProxyPort.getText()));
            } else {
                user.setProxyHost("");
                user.setProxyPort(0);
            }
            clientController.setUser(user);
        }
        try {
            clientController.connectToServer("LOGN");
        } catch (IOException e) {
            e.printStackTrace();
            clientController.display(e.toString(), MESSAGE_TYPE_ADMIN);
        }
    }
}
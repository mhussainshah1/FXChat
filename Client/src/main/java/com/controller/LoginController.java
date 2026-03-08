package com.controller;

import com.common.CommonSettings;
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

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
@Scope("prototype")
public class LoginController {
    private final MainController mainController;
    private final StageService stageService;
    private final User user;
    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ChoiceBox<String> choiceRoom;
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

    //Constructor
    @Autowired
    public LoginController(MainController mainController, StageService stageService, User user) {
        this.mainController = mainController;
        this.stageService = stageService;
        this.user = user;
    }

    //Calls automatically
    @FXML
    private void initialize() throws IOException {
        choiceRoom.setItems(FXCollections.observableArrayList("General", "Teen", "Music", "Party"));
        choiceRoom.setOnAction(event -> {
            roomName = choiceRoom.getValue();
            choiceRoom.setValue(roomName);
        });
    }

    //Event Handler
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

    //Instance Method
    public void loginToChat() {
        if (connect) {
            user.setUserName(txtUserName.getText());
            user.setPassword(txtPassword.getText());
            user.setRoomName(choiceRoom.getValue());
            user.setServerName(txtServerName.getText());
            user.setServerPort(Integer.parseInt(txtServerPort.getText()));
            user.setMaximumGuestNumber(50);
            user.setRoomList(new ArrayList<>(/*List.of("General", "Teen", "Music", "Party")*/));
            user.setProxyState(proxyCheckBox.isSelected());

            if (proxyCheckBox.isSelected()) {
                user.setProxyHost(txtProxyHost.getText());
                user.setProxyPort(Integer.parseInt(txtProxyPort.getText()));
            } else {
                user.setProxyHost("");
                user.setProxyPort(0);
            }
        }
        try {
            mainController.connectToServer("LOGN");
        } catch (IOException e) {
            e.printStackTrace();
            mainController.display(e.toString(), MESSAGE_TYPE_ADMIN);
        }
    }
}
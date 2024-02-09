package com.controller;

import com.common.CommonSettings;
import com.entity.Data;
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
public class SignUpController {
    public PasswordField txtPassword;
    @FXML
    private TextField txtUserName;
    @FXML
    private ChoiceBox<String> choiceRoom;
    @FXML
    private TextField txtServerName;
    @FXML
    private TextField txtServerPort;
    @FXML
    private CheckBox proxyCheckBox;
    @FXML
    private TextField txtProxyHost;
    @FXML
    private TextField txtProxyPort;
    private String roomName = "General";
    private boolean connect;
    @Autowired
    private ClientController clientController;
    @Autowired
    private StageService stageService;

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

    @FXML
    public void actionHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Signup")) {
            connect = true;
            try (var data = new Data("data.properties")) {
                data.setUserName(txtUserName.getText());
                data.setPassword(txtPassword.getText());
                data.setRoomName(choiceRoom.getValue());
                data.setServerName(txtServerName.getText());
                data.setServerPort(Integer.parseInt(txtServerPort.getText()));
                data.setProxyState(proxyCheckBox.isSelected());
                data.setProxyHost(txtProxyHost.getText());
                data.setProxyPort(Integer.parseInt(txtProxyPort.getText()));
            }
            button.getScene().getWindow().hide();
            signupToChat();
        } else if (name.equals("Quit")) {
            connect = false;
            button.getScene().getWindow().hide();
        } else if (name.equals("Log In!")) {
            stageService.changeScene(e, "/com/controller/login.fxml", CommonSettings.PRODUCT_NAME + " - Login", null, null);
        }
    }

    public void signupToChat() {
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
            clientController.connectToServer("SGUP");
        } catch (IOException e) {
            e.printStackTrace();
            clientController.display(e.toString(), MESSAGE_TYPE_ADMIN);
        }
    }
}
package com.controller;

import com.common.CommonSettings;
import com.entity.User;
import com.service.StageService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

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
    private User user;
    private String roomName = "General";
    private boolean connect;
    private ClientController clientController;
    private StageService stageService;

    //Calls automatically
    @FXML
    private void initialize() throws IOException {
        choiceRoom.setItems(FXCollections.observableArrayList("General", "Teen", "Music", "Party"));
        choiceRoom.setOnAction(event -> {
            roomName = choiceRoom.getValue();
            choiceRoom.setValue(roomName);
        });
/*        try (Data data = new Data("data.properties")) {
            txtUserName.setText(data.getUserName());
            txtPassword.setText(data.getPassword());
            txtServerName.setText(data.getServerName());
            txtServerPort.setText(String.valueOf(data.getServerPort()));
            proxyCheckBox.setSelected(data.isProxyState());
            txtProxyHost.setText(data.getProxyHost());
            txtProxyPort.setText(String.valueOf(data.getProxyPort()));
        }*/
        stageService = new StageService();
    }

    @FXML
    public void actionHandler(ActionEvent e) throws IOException {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Signup")) {
            connect = true;
            /*try (var data = new Data("data.properties")) {
                data.setUserName(txtUserName.getText());
                data.setPassword(txtPassword.getText());
                data.setRoomName(roomName);
                data.setServerName(txtServerName.getText());
                data.setServerPort(Integer.parseInt(txtServerPort.getText()));
                data.setProxyState(proxyCheckBox.isSelected());
                data.setProxyHost(txtProxyHost.getText());
                data.setProxyPort(Integer.parseInt(txtProxyPort.getText()));
            }*/
            signupToChat();
            button.getScene().getWindow().hide();
        } else if (name.equals("Quit")) {
            connect = false;
            button.getScene().getWindow().hide();
        } else if (name.equals("Log In!")) {
            stageService.changeScene(e, "/com/controller/login.fxml", CommonSettings.PRODUCT_NAME + " - Login", null, null);
        }
    }

    public void signupToChat() {
        if (connect) {
            user = new User();
            user.setUserName(txtUserName.getText());
            user.setPassword(txtPassword.getText());
            user.setRoomName(choiceRoom.getValue());
            user.setServerName(txtServerName.getText());
            user.setServerPort(Integer.parseInt(txtServerPort.getText()));
            user.setMaximumGuestNumber(50);
            user.setRoomList(new ArrayList<>());
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
            clientController.connectToServer("SGUP");
        } catch (IOException e) {
            e.printStackTrace();
            clientController.display(e.toString(), MESSAGE_TYPE_ADMIN);
        }
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
        stageService.setClientController(clientController);
    }
}

package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class SignUpController {
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

    public void actionHandler(ActionEvent actionEvent) {
    }
}

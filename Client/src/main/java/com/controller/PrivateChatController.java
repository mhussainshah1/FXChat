package com.controller;

import com.client.Message;
import com.controller.tab.TabPaneManagerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.common.CommonSettings.*;

@Component
//@Scope("prototype")
public class PrivateChatController {
    private final ClientController clientController;
    private final TabPaneManagerController tabPaneManagerController;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label lblTitle;
    @FXML
    private ScrollPane sp_main;
    private Message message;
    private String userName;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField txtMessage;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnIgnoreUser;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnEmotions;
    @FXML
    private TextFlow textFlow;
    private boolean visible = false;
    private Stage stage;

    @Autowired
    public PrivateChatController(ClientController clientController, TabPaneManagerController tabPaneManagerController) {
        this.clientController = clientController;
        this.tabPaneManagerController = tabPaneManagerController;
    }

    public void initialize() {
        message = new Message(new Label());
        textFlow.heightProperty().addListener((observable, oldValue, newValue) -> sp_main.setVvalue((Double) newValue));
    }

    @FXML
    private void btnHandler(ActionEvent actionEvent) throws IOException {
        Button button = (Button) actionEvent.getTarget();
        var name = button.getText();
        stage = (Stage) button.getScene().getWindow();

        if (name.equals("Send")) {
            if (!txtMessage.getText().isBlank()) {
                sendMessage();
            }

        } else if (name.equals("Clear")) {
            textFlow.getChildren().clear();

        } else if (actionEvent.getSource().equals(btnIgnoreUser)) {
            tabPaneManagerController.getUsersTabController().ignoreUser(name.equals("Ignore User"), userName);

        } else if (name.equals("Close")) {
            exitPrivateWindow();

        } else if (name.equals("Emotions")) {
            if (visible) {
                visible = false;
                stage.setHeight(PRIVATE_WINDOW_HEIGHT);
            } else {
                visible = true;
                stage.setHeight(PRIVATE_WINDOW_HEIGHT + EMOTION_CANVAS_HEIGHT);
            }
            scrollPane.setVisible(visible);
        }
    }

    public void handleBtnIgnoreUser(String name) {
        if (name.equals("Ignore User")) {
            btnIgnoreUser.setText("Allow User");
            display(userName + " has been ignored!", MESSAGE_TYPE_LEAVE);
        } else {
            btnIgnoreUser.setText("Ignore User");
            display(userName + " has been removed from ignored list!", MESSAGE_TYPE_JOIN);
        }
    }

    @FXML
    private void txtHandler(ActionEvent actionEvent) {
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

    //Instance Methods
    private void sendMessage() throws IOException {
        display(clientController.getUser().getUserName() + ": " + txtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        clientController.sentPrivateMessageToServer(txtMessage.getText(), userName);
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    public void display(String messageText, int messageType) {
        List<Node> nodes = message.parseMessage(messageText, messageType);
        textFlow.getChildren().addAll(nodes);
    }

    public void disableAll() {
        txtMessage.setDisable(true);
        btnSend.setDisable(true);
    }

    public void enableAll() {
        txtMessage.setDisable(false);
        btnSend.setDisable(false);
    }

    // Exit from Private Chat
    public void exitPrivateWindow() {
        tabPaneManagerController.getUsersTabController().removePrivateWindow(userName);
        stage.close();
    }

    //Bean Methods
    public Stage getStage() {
        return (Stage) btnSend.getScene().getWindow();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        lblTitle.setText("Conversation With " + userName);
    }

}

package com.controller;

import com.client.Client;
import com.common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.common.Message.MESSAGE_TYPE_DEFAULT;


public class PrivateChatController {
    @FXML
    private Label lblTitle;
    @FXML
    private VBox root;
    private TilePane tilePane;
    @FXML
    private TextField textField;
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
    private Client client;
    private Message message;
    private String toSend = "Server";

    public void initialize() {
        message = new Message();

        List<Label> buttonList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var icon = new Label(Integer.toString(i), new ImageView(getClass().getResource("/icons/photo" + i + ".gif").toString()));
            icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            icon.setOnMouseClicked(event -> textField.appendText("~~" + icon.getText() + " "));
            icon.setOnMouseEntered(event -> icon.setStyle("-fx-border-color: black"));
            icon.setOnMouseExited(event -> icon.setStyle("-fx-border-color: white"));
            icon.setPadding(new Insets(10));
            icon.setCursor(Cursor.OPEN_HAND);
            buttonList.add(icon);
        }
        tilePane = new TilePane(5, 5);
        tilePane.setPrefColumns(3);
        tilePane.setVisible(visible);
        tilePane.getChildren().addAll(buttonList);
    }

    @FXML private void btnHandler(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getTarget();
        var name = button.getText();
        Stage stage = (Stage) button.getScene().getWindow();

        if (name.equals("Send")) {
            if(!textField.getText().isBlank()) {
                display("You : " + textField.getText() + "\n", MESSAGE_TYPE_DEFAULT);
                sendMessage(textField.getText());
            }

        } else if (name.equals("Clear")) {
            textFlow.getChildren().clear();

        } else if (name.equals("Ignore User")) {

        } else if (name.equals("Close")) {
            stage.hide();

        } else if (name.equals("Emotions")) {
            if (visible) {
                visible = false;
                root.getChildren().remove(tilePane);
                stage.setHeight(300);
            } else {
                visible = true;
                root.getChildren().add(tilePane);
                stage.setHeight(460);
            }
            tilePane.setVisible(visible);
        }
    }

    @FXML private void txtHandler(ActionEvent actionEvent) {
        btnSend.fire();
    }

    //Instance Methods

    private void sendMessage(String messageText) {
        try {
            client.send(new Message(Message.MESSAGE, "@" + toSend + " " + messageText));//@username<space>yourmessage
        } catch (IOException e) {
            e.printStackTrace();
        }
        textField.clear();
        textField.requestFocus();
    }
    private void display(String messageText, int messageType) {
        System.out.println(messageText);
        List<Node> nodes = message.parseMessage(messageText, messageType);
        textFlow.getChildren().addAll(nodes);
    }

    public void setToSend(String toSend) {
        this.toSend = toSend;
    }

    //Bean Methods
    public void setClient(Client client) {
        lblTitle.setText("Conversation with: " + client.getUserName());
        this.client = client;
    }
}

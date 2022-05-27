package com.controller;

import com.client.Client;
import com.common.ChatMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

import static com.common.ChatMessage.MESSAGE_TYPE_DEFAULT;


public class PrivateChatController {
    @FXML private Label lblTitle;
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
    private ChatMessage messageObject;
    private String toSend = "Server";


    public void initialize() {
        messageObject = new ChatMessage();

        List<Label> buttonList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var icon = new Label(Integer.toString(i), new ImageView(getClass().getResource("/icons/photo" + i + ".gif").toString()));
            icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            icon.setOnMouseClicked(event -> textField.appendText("~~" + icon.getText() + " "));
            icon.setOnMouseEntered(event -> icon.setStyle("-fx-border-color: black"));
            icon.setOnMouseExited(event -> icon.setStyle("-fx-border-color: white"));
            icon.setCursor(Cursor.OPEN_HAND);
            buttonList.add(icon);
        }
        tilePane = new TilePane(5, 5);
        tilePane.setPrefColumns(3);
        tilePane.setVisible(visible);
        tilePane.getChildren().addAll(buttonList);
    }

    private void sendMessage(String message) {
        try {
            client.send(new ChatMessage(ChatMessage.MESSAGE,"@"+toSend +" "+message));//@username<space>yourmessage
        } catch (IOException e) {
            e.printStackTrace();
        }
        textField.clear();
        textField.requestFocus();
    }

    public void btnHandler(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getTarget();
        var name = button.getText();
        Stage stage = (Stage) button.getScene().getWindow();

        if (name.equals("Send")) {
            display(textField.getText(), MESSAGE_TYPE_DEFAULT);
            sendMessage(textField.getText());
        }
        if (name.equals("Clear")) {
            textFlow.getChildren().clear();
        }

        if (name.equals("Ignore User")) {

        }
        if (name.equals("Close")) {
            stage.hide();
        }
        if (name.equals("Emotions")) {
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

    private void display(String message, int type) {
        System.out.println(message);
        List<Node> nodes = messageObject.parseMessage(message, type);
        textFlow.getChildren().addAll(nodes);
    }

    public void txtHandler(ActionEvent actionEvent) {
        btnSend.fire();
    }


    public void setClient(Client client) {
        lblTitle.setText("Conversation with: " + client.getUserName());
        this.client = client;
    }

    public void setToSend(String toSend) {
        this.toSend = toSend;
    }

}

package com.controller;

import com.ClientApplication;
import com.client.Client;
import com.common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.List;

import static com.common.Message.*;
import static com.common.CommonSettings.COMPANY_NAME;
import static com.common.CommonSettings.PRODUCT_NAME;


public class ClientController {

    public static final int QUIT_TYPE_DEFAULT = 0,
            QUIT_TYPE_KICK = 1,
            QUIT_TYPE_NULL = 2;
    @FXML
    private GridPane login;
    @FXML
    private ListView<Label> userView;
    @FXML
    private TilePane tilePane;
    @FXML
    private TextFlow messageBoard;
    @FXML
    private Button btnSend;
    @FXML
    private TextField txtMessage;
    @FXML
    private TabPane rightPane;
    @FXML
    private MenuItem loginMenuItem;
    @FXML
    private MenuItem logoutMenuItem;
    private String userName = "Anonymous";
    private String userRoom = "General";
    private int totalUserCount;
    private Client client;
    private LoginController loginController;
    private Message messageObject;

    @FXML
    private void initialize() {
        client = new Client();
        messageObject = new Message();
    }

    //Event Handlers

    @FXML
    private void menuHandler(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.NONE);
        var name = ((MenuItem) e.getTarget()).getText();

        if (name.equals("Login"))
            openLoginWindow();
        else if (name.equals("Logout")) {
            client.disconnectChat();
            disableLogout();
        } else if (name.equals("Exit")) {
            client.disconnectChat();
            Platform.exit();
        } else if (name.equals("About")) {
            alert.setTitle("About Us");
            alert.setHeaderText(PRODUCT_NAME);
            alert.setContentText("\nDeveloped By...\n" + COMPANY_NAME);
            alert.setGraphic(new ImageView(getClass().getResource("/icons/photo13.gif").toString()));
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.show();
        }
    }

    @FXML
    private void btnHandler(ActionEvent e) {
        Button button = (Button) e.getTarget();
        var name = button.getText();

        if (name.equals("Send Message!")) {
            if (!txtMessage.getText().isEmpty())
                sendMessage(txtMessage.getText());
        }
        if (name.equals("Exit Chat")) {
            client.disconnectChat();
            Platform.exit();
        }
    }

    @FXML
    private void txtHandler(ActionEvent e) {
        display("> ", MESSAGE_TYPE_DEFAULT);
        sendMessage(txtMessage.getText());
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

       /* @FXML private void userViewHandler() {
        ObservableList<Label> userTypes = FXCollections.observableArrayList(userView.getItems());
        userView.setItems(userTypes);
        var userSelModel = userView.getSelectionModel();
        userSelModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selItems = "";
            ObservableList<Label> selected = userView.getSelectionModel().getSelectedItems();
            ClientApplication.setUserName(client.getUserName());
            for (Label label : selected) {
                System.out.println(label.getText());
                try {
                    ClientApplication.showPrivateChatStage(label.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

    //Instance Method
    public void openLoginWindow() throws IOException {
        ClientApplication.showLoginStage();
        messageBoard.getChildren().clear();
        display("Connecting To Server... Please Wait...\n", MESSAGE_TYPE_ADMIN);
    }

    public void loginToChat() throws IOException {
        if (loginController.isConnect()) {
            client.setUserName(loginController.getUserName());
            client.setServerName(loginController.getServerName());
            client.setServerPort(loginController.getServerPort());
            if (loginController.isProxyCheckBox()) {
                client.setProxy(true);
                client.setProxyHost(loginController.getProxyHost());
                client.setServerPort(loginController.getProxyPort());
            } else {
                client.setProxy(false);
            }
        }
        connectToServer();
    }

    private void connectToServer() {
        client = createClient();
        try {
            client.startConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        display("""
                Hello.! Welcome to the chatroom.
                Instructions:
                1. Simply type the message to send broadcast to all active clients
                2. Type '@username<space>yourmessage' without quotes to send message to desired client
                3. Type 'LIST' without quotes to see list of active clients
                4. Type 'LOGOUT' without quotes to logoff from main.java.server                       
                """, MESSAGE_TYPE_JOIN
        );
        enableLogin();

        int i = 11;
        var icon = new Label(client.getUserName(), new ImageView(getClass().getResource("/icons/photo" + i + ".gif").toString()));
        icon.setCursor(Cursor.OPEN_HAND);
        userView.getItems().add(icon);

        icon.setOnMouseClicked(e -> {
            Label toSend = (Label) e.getSource();
            if (e.getClickCount() == 2) {
                try {
                    ClientApplication.setUserName(client.getUserName());
                    ClientApplication.showPrivateChatStage(toSend.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    /*
     * To send a message to the text flow
     */

    private void display(String message, int type) {
        System.out.println(message);
        List<Node> nodes = messageObject.parseMessage(message, type);
        messageBoard.getChildren().addAll(nodes);
    }

    private void sendMessage(String message) {
        try {
            if (message.equalsIgnoreCase("LOGOUT")) { // logout if message is LOGOUT
                client.send(new Message(Message.LOGOUT, ""));

            } else if (message.equalsIgnoreCase("LIST")) { // message to check who are present in chatroom
                client.send(new Message(Message.LIST, ""));

            } else { // regular text message
                client.send(new Message(Message.MESSAGE, message));
            }
        } catch (Exception ex) {
            display("Failed to send\n", MESSAGE_TYPE_LEAVE);
            quitConnection(QUIT_TYPE_DEFAULT);
            ex.printStackTrace();
        }
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    private void quitConnection(int quitType) {
        try {
            client.closeConnection(quitType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        disableLogout();
    }

    private void enableLogin() {
        control(true);
    }

    private void disableLogout() {
        messageBoard.getChildren().clear();
        control(false);
        userName = "";
        userRoom = "";
        totalUserCount = 0;
    }

    private void control(boolean status) {
        txtMessage.setEditable(status);
        btnSend.setDisable(!status);
        rightPane.setDisable(!status);
        logoutMenuItem.setDisable(!status);
        loginMenuItem.setDisable(status);
    }

    private Client createClient() {
        return new Client(client.getServerName(), client.getServerPort(), client.getUserName(), data ->
                Platform.runLater(() -> { //UI or background thread - manipulate UI object , It gives control back to UI thread
                    display(data.toString(), MESSAGE_TYPE_DEFAULT);
                }));
    }

    //Bean Methods
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public Client getClient() {
        return client;
    }
}
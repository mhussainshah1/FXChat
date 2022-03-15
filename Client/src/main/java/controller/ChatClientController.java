package controller;

import client.ChatClient;
import client.ClientObject;
import common.MessageObject;
import common.ChatMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import static common.CommonSettings.COMPANY_NAME;
import static common.CommonSettings.PRODUCT_NAME;
import static common.MessageObject.*;

public class ChatClientController implements Runnable {

    public static final int QUIT_TYPE_DEFAULT = 0,
            QUIT_TYPE_KICK = 1,
            QUIT_TYPE_NULL = 2;
    @FXML
    TilePane tilePane;
    @FXML
    TextFlow messageBoard;
    @FXML
    Button btnSend;
    @FXML
    TextField txtMessage;
    @FXML
    TabPane rightPane;
    @FXML
    MenuItem loginMenuItem;
    @FXML
    MenuItem logoutMenuItem;

    private String userName = "Anonymous";
    private String userRoom = "General";
    private int totalUserCount;
    private ChatClient client;
    private Thread thread;
    private boolean keepGoing = true;
    private LoginController loginController;
    private ClientObject clientData;
    private MessageObject messageObject;

    public void initialize() {
        clientData = new ClientObject();
        messageObject = new MessageObject();

/*        List<Button> buttonList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var icon = new Button(Integer.toString(i), new ImageView("icons/photo" + i + ".gif"));
            icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            icon.setOnAction(event -> txtMessage.appendText("~~" + icon.getText() + " "));
            icon.setCursor(Cursor.OPEN_HAND);
            buttonList.add(icon);
        }*/
        List<Label> buttonList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var icon = new Label(Integer.toString(i), new ImageView("icons/photo" + i + ".gif"));
            icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            icon.setOnMouseClicked(event -> txtMessage.appendText("~~" + icon.getText() + " "));
            icon.setOnMouseEntered(event -> icon.setStyle("-fx-border-color: black"));
            icon.setOnMouseExited(event -> icon.setStyle("-fx-border-color: white"));
            icon.setCursor(Cursor.OPEN_HAND);
            buttonList.add(icon);
        }
        tilePane.setPrefColumns(4);
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPadding(new Insets(10));
        tilePane.getChildren().addAll(buttonList);
    }

    //Handlers
    public void menuHandler(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.NONE);
        var name = ((MenuItem) e.getTarget()).getText();

        if (name.equals("Login"))
            createLoginStage();

        if (name.equals("Logout"))
            disconnectChat();

        if (name.equals("Exit")) {
            disconnectChat();
            Platform.exit();
        }

        if (name.equals("About")) {
            alert.setTitle("About Us");
            alert.setHeaderText(PRODUCT_NAME);
            alert.setContentText("\nDeveloped By...\n" + COMPANY_NAME);
            alert.setGraphic(new ImageView("/icons/photo13.gif"));
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.show();
        }
    }

    public void btnHandler(ActionEvent e) {
        var name = ((Button) e.getTarget()).getText();

        if (name.equals("Send Message!")) {
           sendMessage();
        }
        if (name.equals("Exit Chat")) {
            disconnectChat();
            Platform.exit();
        }
    }

    public void txtHandler(ActionEvent e) {
        TextField textField = (TextField) e.getSource();
        String ID = textField.getId();
        if (ID.equals("txtMessage")) {
            btnSend.fire();
        }
    }

    public void createLoginStage() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        loginController = loader.getController();
        loginController.setChatClientController(this);
        Stage loginStage = new Stage();
        loginStage.getIcons().add(new Image("/images/icon.gif"));
        loginStage.setTitle(PRODUCT_NAME + " - Login");
        loginStage.setAlwaysOnTop(true);
        loginStage.setResizable(false);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setScene(new Scene(root, 250, 400));
        loginStage.show();
        //loginToChat();
    }

    void loginToChat() {
        if (loginController.isConnect()) {
            clientData.setUserName(loginController.txtUserName.getText());
            clientData.setServerName(loginController.txtServerName.getText());
            clientData.setServerPort(Integer.parseInt(loginController.txtServerPort.getText()));
            if (loginController.proxyCheckBox.isSelected()) {
                clientData.setProxy(true);
                clientData.setProxyHost(loginController.txtProxyHost.getText());
                clientData.setServerPort(Integer.parseInt(loginController.txtProxyPort.getText()));
            } else {
                clientData.setProxy(false);
            }
        }
        connectToServer();
        System.out.println(clientData);
    }

    public void connectToServer() {
        messageBoard.getChildren().clear();
        messageBoard.getChildren().add(messageObject.getText("Connecting To Server... Please Wait...\n", MESSAGE_TYPE_ADMIN));

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // default values if not entered

        try {
            // create the client.Client object
            client = new ChatClient(clientData.getServerName(), clientData.getServerPort(), clientData.getUserName(), this);
            // try to connect to the main.java.server and return if not connected
            if (!client.start())
                return;

            loginEnable();

            display("""
                    \n Hello.! Welcome to the chatroom.Instructions:
                    1. Simply type the message to send broadcast to all active clients
                    2. Type '@username<space>yourmessage' without quotes to send message to desired client
                    3. Type 'WHOISIN' without quotes to see list of active clients
                    4. Type 'LOGOUT' without quotes to logoff from main.java.server
                    """, MESSAGE_TYPE_JOIN
            );

            // infinite loop to get the input from the user
            String msg = "";

//            Scanner scan = new Scanner(System.in);

            while (keepGoing) {
                if (thread == null) {
                    keepGoing = false;
                    break;
                }
                display("> ", MESSAGE_TYPE_DEFAULT);

                // read message from user
                msg = (String) client.getsInput().readObject();
//                msg = scan.nextLine();

                // logout if message is LOGOUT
                if (msg.equalsIgnoreCase("LOGOUT")) {
                    client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
                    break;
                }
                // message to check who are present in chatroom
                else if (msg.equalsIgnoreCase("WHOISIN")) {
                    client.sendMessage(new ChatMessage(ChatMessage.LIST, ""));
                }
                // regular text message
                else {
                    client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
                }
            }

            //close resource
//            scan.close();

            // client completed its job. disconnect client.
            client.disconnect();
            logoutDisable();

        } catch (Exception e) {
            e.printStackTrace();
            quitConnection(QUIT_TYPE_NULL);
        }
    }

    //methods
    /*
     * To send a message to the console
     */
    public void display(String msg, int type) {
        List<Node> list = new ArrayList<>();
        System.out.print(msg);

        if (msg.contains("~~")) {
            var tokenizer = new StringTokenizer(msg, " ");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                //If its a Proper Image
                if (token.contains("~~")) {
                    int i = Integer.parseInt(token.substring(2));
                    if (i >= 0 && i < 21) {
                        var imageView = new ImageView(new Image("icons/photo" + i + ".gif"));
                        list.add(imageView);
                    }
                } else {
                    messageObject = new MessageObject();
                    list .add(messageObject.getText(token + " ", type));
                }
            }
        } else {
            messageObject = new MessageObject();
            list.add(messageObject.getText(msg, type));
        }
        Platform.runLater(() -> {
            messageBoard.getChildren().addAll(list);
        });
    }

    private void sendMessage() {
        ChatMessage message = new ChatMessage(ChatMessage.MESSAGE, txtMessage.getText());
        sendMessageToServer(message);
        //messageObject.getText(txtMessage.getText(), MESSAGE_TYPE_DEFAULT);
        txtMessage.clear();
        txtMessage.requestFocus();
    }

    private void sendMessageToServer(ChatMessage message) {
        try {
            client.sendMessage(message);
//            dataoutputstream.writeBytes(message + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
            quitConnection(QUIT_TYPE_DEFAULT);
        }
    }

    private void loginEnable() {
        txtMessage.setEditable(true);
        btnSend.setDisable(false);
        rightPane.setDisable(false);
        logoutMenuItem.setDisable(false);
        loginMenuItem.setDisable(true);
    }

    private void disconnectChat() {
        if (client.getSocket() != null) {
            messageObject.getText("CONNECTION TO THE SERVER CLOSED", MESSAGE_TYPE_ADMIN);
            quitConnection(QUIT_TYPE_DEFAULT);
        }
    }

    private void quitConnection(int quitType) {
        if (client.getSocket() != null) {
            try {
                if (quitType == QUIT_TYPE_DEFAULT)
                    client.sendMessage(new ChatMessage(ChatMessage.REMOVE, ""));
                if (quitType == QUIT_TYPE_KICK)
                    client.sendMessage(new ChatMessage(ChatMessage.KICKED_OUT, ""));
                client.getSocket().close();
//                socket = null;
//                tappanel.UserCanvas.ClearAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (thread != null) {
            keepGoing = false;
            thread.interrupt();
            thread = null;
        }
        logoutDisable();
    }

    private void logoutDisable() {
        txtMessage.setEditable(false);
        btnSend.setDisable(true);
        rightPane.setDisable(true);
        logoutMenuItem.setDisable(true);
        loginMenuItem.setDisable(false);

        userName = "";
        userRoom = "";
        totalUserCount = 0;
    }

    public TextFlow getMessageBoard() {
        return messageBoard;
    }

    public void setMessageBoard(TextFlow messageBoard) {
        this.messageBoard = messageBoard;
    }
}
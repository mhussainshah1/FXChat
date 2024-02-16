package com.client;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.common.CommonSettings.*;

/**
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the main.java.server.Server.
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no
 * need to count bytes or to wait for a line feed at the end of the frame
 */

public class Message implements Serializable {
    // The different types of messages sent by the Client
    public static final int LIST = 0,// LIST to receive the list of the users connected
            MESSAGE = 1, // MESSAGE an ordinary text message
            LOGOUT = 2,// LOGOUT to disconnect from the Server
            ROOM = 3,
            ADD = 4,
            EXIST = 5,
            REMOVE = 6,
            KICKED_OUT = 7,
            KICKED_USER_INFO = 8,
            CHANGE_ROOM = 9,
            JOIN_ROOM = 10,
            LEAVE_ROOM = 11,
            ROOM_COUNT = 12,
            PRIVATE_MESSAGE = 13;
    private final int messageType;
    private final Label label;
    private final DateTimeFormatter dateTimeFormatter;
    private List<Node> list;

    // constructor
//    @Autowired
    public Message(Label label) {
        this(label, MESSAGE_TYPE_DEFAULT);
    }

    public Message(Label label, int messageType) {
        this.label = label;
        this.messageType = messageType;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss'> ' ");
        this.list = new ArrayList<>();
    }

    public Text formatMessage(Text text, int messageType) {
        text.setFont(Font.font("Arial", 14));
        switch (messageType) {
            case MESSAGE_TYPE_DEFAULT -> {
                text.setFill(Color.BLACK);
            }
            case MESSAGE_TYPE_JOIN -> {
                text.setFill(Color.BLUE);
            }
            case MESSAGE_TYPE_LEAVE -> {
                text.setFill(Color.RED);
            }
            case MESSAGE_TYPE_ADMIN -> {
                text.setFill(Color.GRAY);
            }
        }
        return text;
    }

    public List<Node> parseMessage(String message, int messageType) {
        list = new ArrayList<>();
        String time = dateTimeFormatter.format(LocalDateTime.now());
        list.add(formatMessage(new Text(time), messageType));

        if (message.contains("~~")) {
            var tokenizer = new StringTokenizer(message, " ");
            while (tokenizer.hasMoreTokens()) {
                var token = tokenizer.nextToken().trim();
                //If it's a Proper Image
                if (token.contains("~~")) {
                    Pattern pattern = Pattern.compile("^~~([0-9]|1[0-9]|20)$");
                    Matcher matcher = pattern.matcher(token);
                    if (matcher.find()) {
                        int i = Integer.parseInt(token.substring(2));
                        var imageView = new ImageView(new Image(getClass().getResource("/icons/photo" + i + ".gif").toString()));
                        list.add(imageView);
                    } else {
                        list.add(formatMessage(new Text(token), messageType));
                    }
                } else {
                    list.add(formatMessage(new Text(token), messageType));
                }
            }
        } else {
            list.add(formatMessage(new Text(message), messageType));
        }
        list.add(new Text(System.lineSeparator())); //new line separator
        return list;
    }

    public Label getLabel() {
        return label;
    }

    public int getMessageType() {
        return messageType;
    }

    @Override
    public String toString() {
        return label.getText();
    }
}
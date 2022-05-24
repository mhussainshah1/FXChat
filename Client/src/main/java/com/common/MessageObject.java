package com.common;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageObject implements Serializable {

    public static final int MESSAGE_TYPE_DEFAULT = 0;
    public static final int MESSAGE_TYPE_JOIN = 1;
    public static final int MESSAGE_TYPE_LEAVE = 2;
    public static final int MESSAGE_TYPE_ADMIN = 3;

    private Text text;
    private int messageType;

    List<Node> list;

    public MessageObject() {
        this.text = new Text();
        this.messageType = MESSAGE_TYPE_DEFAULT;
        list = new ArrayList<>();
    }

    public Text formatMessage(Text text, int messageType) {
        this.text = text;
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

    public List<Node> parseMessage(String message, int type) {
        list = new ArrayList<>();
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
                        list.add(formatMessage(new Text(token), type));
                    }
                } else {
                    list.add(formatMessage(new Text(token), type));
                }
            }
            list.add(new Text(System.lineSeparator())); //new line separator
        } else {
            list.add(formatMessage(new Text(message), type));
        }
        return list;
    }
}
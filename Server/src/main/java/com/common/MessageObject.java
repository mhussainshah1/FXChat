package com.common;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.Serializable;


public class MessageObject implements Serializable {

    public static final int MESSAGE_TYPE_DEFAULT = 0;
    public static final int MESSAGE_TYPE_JOIN = 1;
    public static final int MESSAGE_TYPE_LEAVE = 2;
    public static final int MESSAGE_TYPE_ADMIN = 3;

    private Text text;
    private int messageType;

    public MessageObject() {
        this.text = new Text();
        this.messageType = MESSAGE_TYPE_DEFAULT;
    }

    public Text getText(String Message, int messageType) {
        text.setText(Message);
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
}
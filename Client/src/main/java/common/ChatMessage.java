package common;

import java.io.Serializable;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the main.java.server.Server.
 * When talking from a Java client.Client to a Java main.java.server.Server a lot easier to pass Java objects, no
 * need to count bytes or to wait for a line feed at the end of the frame
 */

public class ChatMessage implements Serializable {

    // The different types of message sent by the client.Client
    // LIST to receive the list of the users connected
    // MESSAGE an ordinary text message
    // LOGOUT to disconnect from the main.java.server.Server
    public static final int LIST = 0,
            MESSAGE = 1,
            LOGOUT = 2,
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

    private final int type;
    private final String message;

    // constructor
    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}

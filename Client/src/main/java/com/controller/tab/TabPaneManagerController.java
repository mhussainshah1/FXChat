package com.controller.tab;

import com.controller.BottomController;
import com.controller.CenterController;
import com.controller.ClientController;
import com.controller.TopController;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TabPaneManagerController {
    @Autowired
    private UsersTabController usersTabController;
    @Autowired
    private RoomsTabController roomsTabController;
    @Autowired
    private CenterController centerController;
    @Autowired
    private BottomController bottomController;
    @Autowired
    private ClientController clientController;

    public void display(String message, int type) {
        clientController.display(message, type);
    }

    public void getRoomUserCount(String roomName) throws IOException {
        clientController.getRoomUserCount(roomName);
    }

    public void changeRoom(String userName, String selectedRoom) throws IOException {
        clientController.changeRoom(userName, selectedRoom);
    }

    //Beans Method
    public RoomsTabController getRoomsTabController() {
        return roomsTabController;
    }

    public TextField getTxtMessage() {
        return bottomController.getTxtMessage();
    }

    public UsersTabController getUsersTabController() {
        return usersTabController;
    }

    public TextFlow getMessageBoard() {
        return centerController.getMessageBoard();
    }
}

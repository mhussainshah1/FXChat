package com.controller.tab;

import com.controller.BottomController;
import com.controller.CenterController;
import com.controller.MainController;
import com.controller.TopController;
import com.entity.User;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.common.CommonSettings.MESSAGE_TYPE_JOIN;

@Component
public class TabPaneManagerController {


    @Autowired
    private UsersTabController usersTabController;
    @Autowired
    private RoomsTabController roomsTabController;
    @Autowired
    TopController topController;
    @Autowired
    private CenterController centerController;
    @Autowired
    private BottomController bottomController;
    @Autowired
    private MainController mainController;
    @Autowired
    User user;

    public void display(String message, int type) {
        mainController.display(message, type);
    }

    public void getRoomUserCount(String roomName) throws IOException {
        mainController.getRoomUserCount(roomName);
    }

    public void changeRoom(String userName, String selectedRoom) throws IOException {
        mainController.changeRoom(userName, selectedRoom);
    }

    public TextField getTxtMessage() {
        return bottomController.getTxtMessage();
    }

    public TextFlow getMessageBoard() {
        return centerController.getMessageBoard();
    }

    public void updateInformationLabel(User user) {
        topController.updateInformationLabel(user);
    }

    //Beans Method
    public RoomsTabController getRoomsTabController() {
        return roomsTabController;
    }

    public UsersTabController getUsersTabController() {
        return usersTabController;
    }
}

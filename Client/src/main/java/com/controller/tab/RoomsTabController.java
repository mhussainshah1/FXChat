package com.controller.tab;

import com.entity.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.common.CommonSettings.MESSAGE_TYPE_ADMIN;

@Component
public class RoomsTabController {
    private final TabPaneManagerController tabPaneManagerController;
    private final User user;
    @FXML
    private ListView<Label> roomView;
    @FXML
    private TextField txtUserCount;
    @FXML
    private Button btnChangeRoom;
    private String selectedRoom;

    @Autowired
    public RoomsTabController(TabPaneManagerController tabPaneManagerController, User user) {
        this.tabPaneManagerController = tabPaneManagerController;
        this.user = user;
    }

    @FXML
    private void initialize() {
        this.selectedRoom = user.getRoomName();
    }

    @FXML
    private void listViewHandler(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            txtUserCount.setText("");
            if (mouseEvent.getSource().equals(roomView)) {
                MultipleSelectionModel<Label> selectionModel = roomView.getSelectionModel();
                ObservableList<Label> selectedItems = selectionModel.getSelectedItems();
                Label label = selectedItems.getFirst();
                selectedRoom = label.getText();
                tabPaneManagerController.getRoomUserCount(selectedRoom);
            }
        }
    }

    @FXML
    private void btnHandler(ActionEvent e) throws IOException {
        var button = (Button) e.getTarget();
        var name = button.getText();
        if (name.equals("Change Room")) {
            //Change Room Coding
            changeRoom();
        }
    }

    // ROOM General Teen Music Party
    public void handleRoom(String[] tokens) {
        //Add User Item into User Canvas
        roomView.getItems().clear();
//        user.getRoomList().clear();
        // Loading Room List in to Room Canvas
        for (int j = 1; j < tokens.length; j++) {
            Label label = new Label(tokens[j], new ImageView(getClass().getResource("/icons/photo13.gif").toString()));
            addRoom(roomView, label);
        }
    }

    // ROCO General 2
    public void handleRoomCount(String[] tokens) {
        String tokenRoomName = tokens[1];
        txtUserCount.setText("Total Users in " + tokenRoomName + " : " + tokens[2]);
    }

    // CHRO Teen
    public void handleChangeRoom(String userRoom) {
        user.setRoomName(userRoom);
    }

    public void addRoom(ListView<Label> listView, Label label) {
        user.getRoomList().add(label.getText());
        listView.getItems().add(label);
    }

    // Function to Change Room
    protected void changeRoom() throws IOException {
        if (selectedRoom.isEmpty()) {
            tabPaneManagerController.display("Invalid Room Selection!", MESSAGE_TYPE_ADMIN);
            return;
        }
        if (selectedRoom.equals(user.getRoomName())) {
            tabPaneManagerController.display("You are already in that ROOM!", MESSAGE_TYPE_ADMIN);
            return;
        }
        tabPaneManagerController.changeRoom(user.getUserName(), selectedRoom);
        tabPaneManagerController.getRoomUserCount(selectedRoom);
    }
}
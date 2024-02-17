package com.controller;

import com.entity.Client;
import com.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import static com.common.CommonSettings.*;

@Component
public class TopController {

    public MenuItem loginMenuItem;
    public MenuItem signupMenuItem;
    public MenuItem logoutMenuItem;
    public Label informationLabel;
    @Autowired
    ClientController clientController;
    @Autowired
    private User user;
    @FXML
    public void initialize() {

    }

    @FXML
    private void menuHandler(ActionEvent e) throws IOException {
        var alert = new Alert(Alert.AlertType.NONE);
        var name = ((MenuItem) e.getTarget()).getText();

        switch (name) {
            case "Login" -> clientController.openLoginWindow();
            case "Signup" -> clientController.openSignupWindow();
            case "Logout" -> clientController.quitConnection(QUIT_TYPE_DEFAULT);
            case "Exit" -> clientController.shutdown();
            case "About" -> {
                alert.setTitle("About Us");
                alert.setHeaderText(PRODUCT_NAME);
                alert.setContentText("\nDeveloped By...\n" + COMPANY_NAME);
                alert.setGraphic(new ImageView(getClass().getResource("/icons/photo13.gif").toString()));
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.show();
            }
        }
    }

    //Function To Update the Information Label
    public void updateInformationLabel() {
        String builder = "User Name: " + user.getUserName() + "\t\t" +
                "Room Name: " + user.getRoomName() + "\t\t" +
                "No. Of Users: " + user.getClients().size() + "\t\t";
        informationLabel.setText(builder);

        try {
            clientController.getRoomUserCount(user.getRoomName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("ChatClientController - users [");
        for (Client client : user.getClients()) {
            System.out.print(client.getClientName() + " ");
        }
        System.out.println("]");
    }
}

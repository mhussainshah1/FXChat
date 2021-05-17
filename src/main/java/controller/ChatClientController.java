package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;

import static client.CommonSettings.COMPANY_NAME;
import static client.CommonSettings.PRODUCT_NAME;

public class ChatClientController {

    public void menuHandler(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        var name = ((MenuItem) e.getTarget()).getText();

        if (name.equals("Exit"))
            Platform.exit();

        if (name.equals("About")) {
            alert.setTitle("About Us");
            alert.setHeaderText(PRODUCT_NAME);
            alert.setContentText("\nDeveloped By...\n" + COMPANY_NAME);
            alert.setGraphic(new ImageView("icons/photo13.gif"));
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.show();
        }
    }

    public void btnHandler(ActionEvent e) {
        var name = ((Button) e.getTarget()).getText();

        if (name.equals("Exit Chat")) {
            Platform.exit();
        }
    }
}
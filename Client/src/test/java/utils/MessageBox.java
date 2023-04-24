package utils;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class MessageBox extends Application {

    public static void main(String[] args) {
        // launch the application
        launch(args);
    }

    // launch the application
    public void start(Stage s) {
        // set title for the stage
        s.setTitle("creating alerts");

        // create a button
        var b = new Button("Confirmation alert");
        var b1 = new Button("error alert");
        var b2 = new Button("Information alert");
        var b3 = new Button("Warning alert");

        // create a tile pane
        var r = new TilePane();

        // create a alert
        var a = new Alert(AlertType.NONE);

        // action event
        EventHandler<ActionEvent> event = e -> {
            // set alert type
            a.setAlertType(AlertType.CONFIRMATION);

            // show the dialog
            a.show();
        };

        // action event
        EventHandler<ActionEvent> event1 = e -> {
            // set alert type
            a.setAlertType(AlertType.ERROR);

            // show the dialog
            a.show();
        };

        // action event
        EventHandler<ActionEvent> event2 = e -> {
            // set alert type
            a.setAlertType(AlertType.INFORMATION);

            // show the dialog
            a.show();
        };

        // action event
        EventHandler<ActionEvent> event3 = e -> {
            // set alert type
            a.setAlertType(AlertType.WARNING);

            // show the dialog
            a.show();
        };

        // when button is pressed
        b.setOnAction(event);
        b1.setOnAction(event1);
        b2.setOnAction(event2);
        b3.setOnAction(event3);

        // add button
        r.getChildren().add(b);
        r.getChildren().add(b1);
        r.getChildren().add(b2);
        r.getChildren().add(b3);

        // create a scene
        Scene sc = new Scene(r, 200, 200);

        // set the scene
        s.setScene(sc);

        s.show();
    }
}
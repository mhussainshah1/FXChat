package utils;

// Java Program to create alert and set different alert types and button type and also set different content text

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class DialogDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        //Child Node
        Alert a = new Alert(AlertType.NONE);
        Button b = new Button("Confirmation alert");
        b.setOnAction(e -> {
            a.setAlertType(AlertType.CONFIRMATION);
            a.setContentText("ConfirmationDialog");
            a.show();
        });

        Button b1 = new Button("error alert");
        b1.setOnAction(e -> {
            a.setAlertType(AlertType.ERROR);
            a.setContentText("error Dialog");
            a.show();
        });

        Button b2 = new Button("Information alert");
        b2.setOnAction(e -> {
            a.setAlertType(AlertType.INFORMATION);
            a.setContentText("Information Dialog");
            a.show();
        });

        Button b3 = new Button("Warning alert");
        b3.setOnAction(e -> {
            a.setAlertType(AlertType.WARNING);
            a.setContentText("Warning Dialog");
            a.show();
        });

        Button b4 = new Button("none alert");
        b4.setOnAction(e -> {
            Alert a1 = new Alert(AlertType.NONE, "default Dialog", ButtonType.APPLY);
            a1.show();
        });

        //Parent Node
        var rootNode = new TilePane();
        rootNode.getChildren().addAll(b, b1, b2, b3, b4);

        //Scene
        var scene = new Scene(rootNode, 200, 200);

        //Stage
        primaryStage.setTitle("creating alerts");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
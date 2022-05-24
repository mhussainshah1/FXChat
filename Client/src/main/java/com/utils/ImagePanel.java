package com.utils;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ImagePanel extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Child Nodes
        List<Button> buttonList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var icon = new Button(Integer.toString(i),new ImageView(getClass().getResource(  "/icons/photo" + i + ".gif").toString()));
            icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            icon.setOnAction(event -> System.out.println(icon.getText()));
            icon.setCursor(Cursor.OPEN_HAND);
            buttonList.add(icon);
        }

        //Parent Node
        var rootNode = new FlowPane(10,10);
//        rootNode.setAlignment(Pos.CENTER);
        rootNode.getChildren().addAll(buttonList);

        //Scene
        var scene = new Scene(rootNode, 350, 200);

        //Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Image Panel");
        primaryStage.getIcons().add(new Image(getClass().getResource("/images/icon.gif").toString()));
        primaryStage.show();
    }
}

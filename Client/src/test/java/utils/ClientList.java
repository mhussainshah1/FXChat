package utils;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ClientList extends Application {
    Label response;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Child Node
        response = new Label("Select Client");
        List<Label> labelList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var icon = new Label(i + ".gif", new ImageView(getClass().getResource(   "/icons/photo" + i + ".gif").toString()));
//            icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            icon.setCursor(Cursor.OPEN_HAND);
            labelList.add(icon);
        }
        ObservableList<Label> transportTypes = FXCollections.observableArrayList(labelList);
        var lvTransport = new ListView<>(transportTypes);
        lvTransport.setPrefSize(400, 400);
        lvTransport.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        var lvSelModel = lvTransport.getSelectionModel();
        lvSelModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selItems = "";
            ObservableList<Label> selected = lvTransport.getSelectionModel().getSelectedItems();
            for (Label label : selected) {
                selItems += "\n      " + label.getText();
            }
            response.setText("All transport selected: " + selItems);
            var privateChat = new PrivateChat();
        });

        //Parent
        var rootNode = new VBox(10);
        rootNode.setAlignment(Pos.CENTER);
        rootNode.getChildren().addAll(lvTransport, response);

        //Scene
        var scene = new Scene(rootNode, 300, 600);

        //Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("ListView Demo");
        primaryStage.show();
    }
}
package utils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class TextFlowDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new Insets(10));
        textFlow.setLineSpacing(10);


        VBox container = new VBox();
        TextField textField = new TextField();

        Button button = new Button("Send");
        button.setPrefWidth(70);

        // Textfield re-sizes according to VBox
        textField.prefWidthProperty().bind(container.widthProperty().subtract(button.prefWidthProperty()));

        button.setOnAction(e -> {
            Text text;
            if (textFlow.getChildren().size() == 0) {
                text = new Text(textField.getText());
            } else {
                // Add new line if not the first child
                text = new Text("\n" + textField.getText());
            }
            if (textField.getText().contains("~~")) {
                int i = 0;
                var imageView = new ImageView(new Image("icons/photo" + i + ".gif"));
                // Remove :) from text
                text.setText(text.getText().replace("~~", " "));
                textFlow.getChildren().addAll(text, imageView);
            } else {
                textFlow.getChildren().add(text);
            }
            textField.clear();
            textField.requestFocus();
        });


        // On Enter press
        textField.setOnAction(e -> button.fire());

        container.getChildren().addAll(textFlow, new HBox(textField, button));
        VBox.setVgrow(textFlow, Priority.ALWAYS);


        Scene scene = new Scene(container, 300, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
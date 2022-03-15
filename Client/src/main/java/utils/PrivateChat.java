package utils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PrivateChat extends Application {
    String name = "ali";
    TilePane tp;
    boolean visible = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var rootNode = new VBox();

        var lblTile = new Label("Conversation with " + name);

        var textFlow = new TextFlow();
        textFlow.setPadding(new Insets(10));
        textFlow.setLineSpacing(10);
        textFlow.setPrefSize(310, 190);
        textFlow.setStyle("-fx-background-color: white;");
        var scrollPane = new ScrollPane(textFlow);
//        scrollPane.setPrefViewportWidth(310);
//        scrollPane.setPrefViewportHeight(190);
//        scrollPane.setPannable(true);

        var textField = new TextField();

        var btnSend = new Button("Send");
        btnSend.setPrefWidth(70);
        btnSend.setOnAction(event -> {
                    Text text;
                    if (textFlow.getChildren().size() == 0) {
                        text = new Text(name + ": " + textField.getText());
                    } else { // Add new line if not the first child
                        text = new Text("\n" + name + ": " + textField.getText());
                    }
                    if (textField.getText().contains("~~")) {
                        text = new Text("\n" + name);
                        textFlow.getChildren().add(text);
                        var tokenizer = new StringTokenizer(textField.getText(), " ");
                        while (tokenizer.hasMoreTokens()) {
                            String token = tokenizer.nextToken();
                            //If it's a Proper Image
                            int i = Integer.parseInt(token.substring(2));
                            var imageView = new ImageView(new Image("icons/photo" + i + ".gif"));
//                            text.setText(text.getText().replace("~~" + i, ""));
                            textFlow.getChildren().addAll(/*text,*/ imageView);
                        }
                    } else {
                        textFlow.getChildren().add(text);
                    }
                    textField.clear();
                    textField.requestFocus();
                }
        );

        textField.prefWidthProperty().bind(rootNode.widthProperty().subtract(btnSend.prefWidthProperty()));
        textField.setOnAction(event -> btnSend.fire());

        var flowPane1 = new HBox();
        flowPane1.setAlignment(Pos.CENTER);
        flowPane1.getChildren().addAll(textField, btnSend);

        var btnClear = new Button("Clear");
        btnClear.setOnAction(e -> textFlow.getChildren().clear());

        var btnIgnoreUser = new Button("Ignore User");

        var btnClose = new Button("Close");
        btnClose.setOnAction(e -> System.exit(0));

        var btnEmotion = new Button("Emotions");
        btnEmotion.setOnAction(e -> {
            if (visible) {
                visible = false;
                rootNode.getChildren().remove(tp);
                primaryStage.setHeight(300);
            } else {
                visible = true;
                rootNode.getChildren().add(tp);
                primaryStage.setHeight(460);
            }
            tp.setVisible(visible);
        });

        var flowPane2 = new FlowPane(10, 10);
        flowPane2.setAlignment(Pos.CENTER);
        flowPane2.getChildren().addAll(btnClear, btnIgnoreUser, btnClose, btnEmotion);

        List<Label> buttonList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            var icon = new Label(Integer.toString(i), new ImageView("icons/photo" + i + ".gif"));
            icon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            icon.setOnMouseClicked(event -> textField.appendText("~~" + icon.getText() + " "));
            icon.setOnMouseEntered(event -> icon.setStyle("-fx-border-color: black"));
            icon.setOnMouseExited(event -> icon.setStyle("-fx-border-color: white"));
            icon.setCursor(Cursor.OPEN_HAND);
            buttonList.add(icon);
        }
        tp = new TilePane(5, 5);
        tp.setPrefColumns(3);
        tp.setVisible(visible);
        tp.getChildren().addAll(buttonList);

        rootNode.getChildren().addAll(lblTile, scrollPane, flowPane1, flowPane2);
        var scene = new Scene(rootNode, 310, 270);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Private Chat with " + name);
        primaryStage.show();
    }
}

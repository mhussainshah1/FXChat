import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static client.CommonSettings.PRODUCT_NAME;

public class ChatClient extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/chatclient.fxml"));
        primaryStage.getIcons().add(new Image("images/icon.gif"));
        primaryStage.setScene(new Scene(root, 778,575 ));
        primaryStage.setTitle(PRODUCT_NAME);
        primaryStage.show();
    }
}
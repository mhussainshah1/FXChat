import controller.ChatClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static client.CommonSettings.PRODUCT_NAME;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/fxml/chatclient.fxml"));
        Parent root = loader.load();
        ChatClientController chatClientController = loader.getController();
        primaryStage.getIcons().add(new Image("images/icon.gif"));
        primaryStage.setScene(new Scene(root, 778, 575));
        primaryStage.setTitle(PRODUCT_NAME);
        primaryStage.show();
        chatClientController.createLoginStage();
    }
}

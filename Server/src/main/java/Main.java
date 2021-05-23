import controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/fxml/server.fxml"));
        Parent root = loader.load();
        ServerController serverController = loader.getController();
        primaryStage.getIcons().add(new Image("/images/icon.gif"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Chat Server");
        primaryStage.show();
    }
}

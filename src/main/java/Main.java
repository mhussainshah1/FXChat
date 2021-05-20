import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static client.CommonSettings.PRODUCT_NAME;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/chatclient.fxml"));
        primaryStage.getIcons().add(new Image("images/icon.gif"));
        primaryStage.setScene(new Scene(root, 778, 575));
        primaryStage.setTitle(PRODUCT_NAME);
        primaryStage.show();

        createLoginStage();
    }

    private void createLoginStage() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

//        LoginController loginController = loader.getController();
      //  loginController.initialize();

        Stage loginStage = new Stage();
        loginStage.getIcons().add(new Image("images/icon.gif"));
        loginStage.setTitle(PRODUCT_NAME + " - Login");
        loginStage.setAlwaysOnTop(true);
        loginStage.setResizable(false);
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setScene(new Scene(root, 250, 400));
        loginStage.show();
    }
}

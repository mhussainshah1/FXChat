package com;

import com.controller.tab.ConnectionTabController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServerApplication extends Application {

    @Autowired
    private ConnectionTabController connectionTabController;

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = SpringApplication.run(ServerApplication.class);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/com/controller/main.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        primaryStage.getIcons().add(new Image(getClass().getResource("/images/icon.gif").toString()));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Chat Server");
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        connectionTabController.shutdown();
        Platform.exit();
    }
}
package com;

import com.common.CommonSettings;
import com.controller.MainController;
import javafx.application.Application;
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
public class ClientApplication extends Application {

//    @Autowired
    private MainController mainController;
    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        Application.launch(ClientApplication.class, args);
    }

    @Override
    public void init() {
        springContext = SpringApplication.run(ClientApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/controller/main.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();
        mainController = fxmlLoader.getController();

        primaryStage.getIcons().add(new Image(getClass().getResource("/images/icon.gif").toString()));
        primaryStage.setTitle(CommonSettings.PRODUCT_NAME);
        primaryStage.setScene(new Scene(root, 778, 575));
        primaryStage.setResizable(false);
        primaryStage.show();
        mainController.openLoginWindow();
    }

    @Override
    public void stop() {
        springContext.close();
        mainController.shutdown();
    }
}
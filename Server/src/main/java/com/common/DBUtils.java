package com.common;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DBUtils {

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String favChannel) {
        Parent root = null;

        if (username != null && favChannel != null) {
            try {
                var loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
//                WelcomeController welcomeController = loader.getController();
//                welcomeController.setUserInformation(username, favChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Scene scene = ((Node) event.getSource()).getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String password, String favChannel) {

        ResultSet resultSet = null;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx_app", "root", "password");
             PreparedStatement psCheckUserExits = connection.prepareStatement("""
                     SELECT *
                     FROM users
                     WHERE username = ?
                     """);
             PreparedStatement psInsert = connection.prepareStatement("""
                     INSERT INTO users (username, password, favchannel)
                     VALUES(?,?,?)
                     """);) {

            psCheckUserExits.setString(1, username);
            resultSet = psCheckUserExits.executeQuery();

            if (resultSet.isBeforeFirst()) {
                System.out.println("User already exist");
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username.");
                alert.show();
            } else {
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, favChannel);
                psInsert.executeUpdate();
                changeScene(event, "welcome.fxml", "Welcome!", username, favChannel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void logInUser(ActionEvent event, String username, String password) {
        ResultSet resultSet = null;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx_app", "root", "password");
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT password, favChannel 
                     FROM users 
                     WHERE username = ?
                     """);) {
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) { //if it doesn't exist in the database
                System.out.println("User not found in the database!");
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();
            } else {
                while (resultSet.next()) {
                    var retrievePassword = resultSet.getString("password");
                    var retrievedChannel = resultSet.getString("favChannel");
                    if (retrievePassword.equals(password)) {
                        changeScene(event, "welcome.fxml", "Welcome!", username, retrievedChannel);
                    } else {
                        System.out.println("Password didn't match");
                        var alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("The provided credentials are incorrect!");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

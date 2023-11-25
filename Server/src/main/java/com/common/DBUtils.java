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

    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username, String room) {
        Parent root = null;

        if (username != null && room != null) {
            try {
                var loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
//                WelcomeController welcomeController = loader.getController();
//                welcomeController.setUserInformation(username, room);
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

    public static void signUpUser(String username, String password, String room) throws SQLException {

        ResultSet resultSet = null;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx_app", "root", "password");
             PreparedStatement psCheckUserExits = connection.prepareStatement("""
                     SELECT *
                     FROM users
                     WHERE username = ?
                     """);
             PreparedStatement psInsert = connection.prepareStatement("""
                     INSERT INTO users (username, password, room)
                     VALUES(?,?,?)
                     """)) {

            psCheckUserExits.setString(1, username);
            resultSet = psCheckUserExits.executeQuery();

            if (resultSet.isBeforeFirst()) {
                throw new SQLException("User already exist");
            } else {
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.setString(3, room);
                psInsert.executeUpdate();
                //changeScene(event, "welcome.fxml", "Welcome!", username, room);
            }
        }
    }

    public static void logInUser(String username, String password) throws SQLException {
        ResultSet resultSet = null;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafx_app", "root", "password");
             PreparedStatement preparedStatement = connection.prepareStatement("""
                     SELECT password, room
                     FROM users
                     WHERE username = ?
                     """)) {
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) { //if it doesn't exist in the database
                throw new SQLException("User not found in the database!");
            } else {
                while (resultSet.next()) {
                    var retrievePassword = resultSet.getString("password");
                    var retrievedRoom = resultSet.getString("room");
                    if (retrievePassword.equals(password)) {
                        System.out.println("DBUtils: Password Match!");
                        //changeScene(event, "welcome.fxml", "Welcome!", username, retrievedChannel);
                    } else {
                        throw new SQLException("Password didn't match");
                    }
                }
            }
        }
    }
}
package com.service;

import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class DatabaseService {

    public void signUpUser(String username, String password, String room) throws SQLException {

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
            }
        }
    }

    public void logInUser(String username, String password) throws SQLException {
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
                        System.out.println("DatabaseService: Password Match!");
                    } else {
                        throw new SQLException("Password didn't match");
                    }
                }
            }
        }
    }
}
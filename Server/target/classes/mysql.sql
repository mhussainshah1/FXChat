-- create database javafx_app;
use javafx_app;

drop table javafx_app.users;

create table users
(
    user_id    INT NOT NULL AUTO_INCREMENT,
    username   VARCHAR(45),
    password   VARCHAR(45),
    room VARCHAR(45),
    PRIMARY KEY (user_id)
);
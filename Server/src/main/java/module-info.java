open module server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;

//    opens com to javafx.fxml;
    exports com;

//    opens com.controller to javafx.fxml;
    exports com.controller;
}
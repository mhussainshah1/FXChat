open module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;

//    opens com to javafx.fxml;
    exports com;

//    opens com.client to javafx.fxml, test;
    exports com.client;

//    opens com.common to javafx.fxml;
    exports com.common;

//    opens com.controller to javafx.fxml;
    exports com.controller;

}
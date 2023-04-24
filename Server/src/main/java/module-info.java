module server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com to javafx.fxml;
    exports com;

    opens com.controller to javafx.fxml;
    exports com.controller;
}
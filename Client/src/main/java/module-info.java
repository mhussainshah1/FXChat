module client {
    requires javafx.controls;
    requires javafx.fxml;

    opens com to javafx.fxml;
    exports com;

    opens com.client to javafx.fxml, test;
    exports com.client;

    opens com.common to javafx.fxml;
    exports com.common;

    opens com.controller to javafx.fxml;
    exports com.controller;

}
module test {
    requires javafx.controls;
    requires javafx.fxml;

    opens utils to javafx.fxml;
    exports utils;

}
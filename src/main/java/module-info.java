module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires java.desktop;

    opens main to javafx.fxml;
    exports main;
}

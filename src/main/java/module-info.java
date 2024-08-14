module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;

    requires java.desktop;
    requires jakarta.mail;

    opens main to javafx.fxml;
    exports main;
}

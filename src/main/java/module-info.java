module lk.ijse.hotelmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires mysql.connector.j;
    requires javafx.graphics;
    requires java.management;
    requires com.google.protobuf;
    requires java.mail;

    opens lk.ijse.hotelmanagementsystem.controller to javafx.fxml;
    opens lk.ijse.hotelmanagementsystem.dto.tm to javafx.base;
    exports lk.ijse.hotelmanagementsystem;
    exports lk.ijse.hotelmanagementsystem.controller;
}
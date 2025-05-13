module lk.ijse.hotelmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens lk.ijse.hotelmanagementsystem.controller to javafx.fxml;
    exports lk.ijse.hotelmanagementsystem;
}
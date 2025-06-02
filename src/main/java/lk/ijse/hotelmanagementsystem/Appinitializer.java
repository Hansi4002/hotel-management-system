package lk.ijse.hotelmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Appinitializer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent load = FXMLLoader.load(getClass().getResource("/view/ReservationTableView.fxml"));
        Scene scene = new Scene(load);
        stage.setScene(scene);
        stage.setTitle("Hotel Management System");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
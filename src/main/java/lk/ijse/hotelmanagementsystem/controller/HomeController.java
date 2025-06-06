package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    public ImageView hotelLogo;
    public Button btnGuests;
    public Button btnReservations;
    public Button btnDashboard;

    public void navigateToGuests(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GuestTableView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnGuests.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hotel Management System - Guests");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK).show();
        }
    }

    public void navigateToReservations(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReservationTableView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnReservations.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hotel Management System - Reservations");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK).show();
        }
    }

    public void navigateToDashboard(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hotel Management System - Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK).show();
        }

    }
}

package lk.ijse.hotelmanagementsystem.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lk.ijse.hotelmanagementsystem.model.DashboardModel;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Label lblWelcomeMessage;
    @FXML private Label lblTotalRooms;
    @FXML private Label lblAvailableRooms;
    @FXML private Label lblOccupiedRooms;
    @FXML private Label lblTotalBookings;
    @FXML private Label lblActiveBookings;
    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTodayRevenue;
    @FXML private AnchorPane contentArea;
    @FXML private VBox chartContainer;

    private final DashboardModel dashboardModel = new DashboardModel();
    private String userName = "Admin";

    @FXML
    public void initialize() throws IOException {
        loadDashboardStats();
        updateDateTime();
        loadCharts();
    }

    private void loadDashboardStats() {
        try {
            lblTotalRooms.setText(String.valueOf(dashboardModel.getTotalRooms()));
            lblAvailableRooms.setText(String.valueOf(dashboardModel.getAvailableRooms()));
            lblOccupiedRooms.setText(String.valueOf(dashboardModel.getOccupiedRooms()));

            lblTotalBookings.setText(String.valueOf(dashboardModel.getTotalReservations()));
            lblActiveBookings.setText(String.valueOf(dashboardModel.getActiveReservations()));

            lblTotalRevenue.setText(String.format("Rs. %,.2f", dashboardModel.getTotalRevenue()));
            lblTodayRevenue.setText(String.format("Rs. %,.2f", dashboardModel.getTodayRevenue()));

        } catch (Exception e) {
            e.printStackTrace();
            setErrorLabels();
        }
    }

    private void setErrorLabels() {
        lblTotalRooms.setText("Error");
        lblAvailableRooms.setText("Error");
        lblOccupiedRooms.setText("Error");
        lblTotalBookings.setText("Error");
        lblActiveBookings.setText("Error");
        lblTotalRevenue.setText("Error");
        lblTodayRevenue.setText("Error");
    }

    private void updateDateTime() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Colombo"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a, EEEE, MMMM dd, yyyy");
            String formattedDateTime = zonedDateTime.format(formatter);
            lblWelcomeMessage.setText(String.format("Welcome back, %s! %s", userName, formattedDateTime));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void loadCharts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReservationTableView.fxml"));
            Parent chartContent = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(chartContent);

            AnchorPane.setTopAnchor(chartContent, 0.0);
            AnchorPane.setRightAnchor(chartContent, 0.0);
            AnchorPane.setBottomAnchor(chartContent, 0.0);
            AnchorPane.setLeftAnchor(chartContent, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void refreshDashboard() throws IOException {
        chartContainer.getChildren().clear();
        loadDashboardStats();
        loadCharts();
    }
}
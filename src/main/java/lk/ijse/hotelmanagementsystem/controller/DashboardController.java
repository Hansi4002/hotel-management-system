package lk.ijse.hotelmanagementsystem.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lk.ijse.hotelmanagementsystem.model.DashboardModel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML
    private Label lblWelcomeMessage;

    @FXML
    private Label lblTotalRooms;

    @FXML
    private Label lblTotalBookings;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private AnchorPane contentArea;

    private final DashboardModel dashboardModel = new DashboardModel();
    private String userName = "Admin";

    @FXML
    public void initialize() {
        loadDashboardStats();

        updateDateTime();

        Label placeholder = new Label("Chart or Table will be added here.");
        contentArea.getChildren().add(placeholder);
    }

    private void loadDashboardStats() {
        try {
            lblTotalRooms.setText(String.valueOf(dashboardModel.getTotalRooms()));
            lblTotalBookings.setText(String.valueOf(dashboardModel.getTotalReservations()));
            lblTotalRevenue.setText(String.format("RS,d", dashboardModel.getTotalRevenue()));
        } catch (Exception e) {
            e.printStackTrace();
            lblTotalRooms.setText("Error");
            lblTotalBookings.setText("Error");
            lblTotalRevenue.setText("Error");
        }
    }

    private void updateDateTime() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Colombo")); // +0530 timezone
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a Z, EEEE, MMMM dd, yyyy");
            String formattedDateTime = zonedDateTime.format(formatter);
            lblWelcomeMessage.setText(String.format("Welcome back, %s! Today is %s.", userName, formattedDateTime));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
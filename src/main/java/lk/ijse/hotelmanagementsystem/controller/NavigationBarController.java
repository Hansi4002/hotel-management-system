package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationBarController {
    public MenuBar menuBar;
    public MenuItem menuHome;
    public MenuItem menuProfile;
    public MenuItem menuGuest;
    public MenuItem menuRoom;
    public MenuItem menuReservation;
    public MenuItem menuFood;
    public MenuItem menuFoodOrder;
    public MenuItem menuFoodOrderDetails;
    public MenuItem menuSupplier;
    public MenuItem menuSupplierFood;
    public MenuItem menuPayment;
    public MenuItem menuMaintenance;
    public Button btnLogout;
    public Button btnSearch;
    public TextField txtSearch;
    public MenuItem menuStaff;

    public void navigateToView(ActionEvent actionEvent) {
        MenuItem clickedItem = (MenuItem) actionEvent.getSource();
        String fxmlPath = (String) clickedItem.getUserData();

        if (fxmlPath != null) {
            loadView(fxmlPath);
        }
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();
    }

    public void btnsearchOnAction(ActionEvent actionEvent) {
        onSearch(null);
    }

    private void onSearch(Object o) {
        String query = txtSearch.getText().toLowerCase();
        if (query.contains("home")) {
            loadView("HomeView.fxml");
        } else if (query.contains("profile")) {
            loadView("ProfileView.fxml");
        } else if (query.contains("guest")) {
            loadView("GuestView.fxml");
        } else if (query.contains("room")) {
            loadView("RoomView.fxml");
        } else if (query.contains("reservation")) {
            loadView("ReservationView.fxml");
        } else if (query.contains("food")) {
            loadView("FoodView.fxml");
        } else if (query.contains("order details")) {
            loadView("FoodOrderDetailView.fxml");
        } else if (query.contains("order")) {
            loadView("FoodOrderView.fxml");
        } else if (query.contains("supplier food")) {
            loadView("SupplierFoodView.fxml");
        } else if (query.contains("supplier")) {
            loadView("SupplierView.fxml");
        } else if (query.contains("payment")) {
            loadView("PaymentView.fxml");
        } else if (query.contains("maintenance")) {
            loadView("MaintenanceView.fxml");
        }else if (query.contains("staff")) {
            loadView("StaffView.fxml");
        }
    }

    private void loadView(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/" + fxmlPath));
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

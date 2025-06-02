package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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

    public void navigateToView(ActionEvent actionEvent) {
        MenuItem clickedItem = (MenuItem) actionEvent.getSource();
        String fxmlPath = (String) clickedItem.getUserData();

        if (fxmlPath != null) {
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

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();
    }
}

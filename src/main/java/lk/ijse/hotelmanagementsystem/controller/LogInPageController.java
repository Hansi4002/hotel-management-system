package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.UserDTO;
import lk.ijse.hotelmanagementsystem.model.LogInPageModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LogInPageController implements Initializable {

    public TextField txtUsername;
    public PasswordField txtPassword;
    public Button btnForgotPass;
    public Button btnLogin;
    public Label lblError;

    private LogInPageModel loginModel;

    public void btnForgotPassOnAction(ActionEvent actionEvent) {
        String username = txtUsername.getText().trim();

        if (username.isEmpty()) {
            lblError.setText("Please enter your username");
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter your username to reset password");
            return;
        }

        try {
            UserDTO user = loginModel.getUserByUsername(username);
            if (user != null) {
                Parent root = FXMLLoader.load(getClass().getResource("/view/ForgotPassword.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Hotel Management System - Reset Password");
                stage.centerOnScreen();
                stage.show();
            } else {
                lblError.setText("User not found");
                showAlert(Alert.AlertType.ERROR, "Error", "No user found with username: " + username);
            }
        } catch (IOException e) {
            lblError.setText("Failed to load reset password page");
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load reset password page: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loginOnAction(ActionEvent actionEvent) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Username and password are required");
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter both username and password");
            return;
        }

        try {
            UserDTO user = loginModel.login(username, password);
            if (user != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Login Successful! Welcome " + user.getRole());

                Parent root = FXMLLoader.load(getClass().getResource("/view/HomeView.fxml"));
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Hotel Management System - Dashboard");
                stage.centerOnScreen();
            } else {
                lblError.setText("Invalid credentials");
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid username or password");
            }
        } catch (SQLException e) {
            lblError.setText("Database error");
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            lblError.setText("Failed to load dashboard");
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginModel = new LogInPageModel();
        lblError.setText("");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
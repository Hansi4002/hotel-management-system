package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.UserDTO;
import lk.ijse.hotelmanagementsystem.model.LogInPageModel;

import java.io.IOException;
import java.sql.SQLException;

public class LogInPageController {

    public Button btnSignIn;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public void logInAction(ActionEvent actionEvent) {
        String email = txtUserName.getText();
        String password = txtPassword.getText();

        try {
            UserDTO user = LogInPageModel.login(email, password);
            if (user != null) {
                new Alert(Alert.AlertType.INFORMATION, "Login Successful! Welcome " + user.getRole()).show();

                Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
                Stage stage = (Stage) btnSignIn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();

            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid credentials!").show();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }
}

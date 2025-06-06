package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.UserDTO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    public Button btnChangePic;
    public Label lblUsername;
    public Label lblUserRole;
    public TextField txtUserId;
    public TextField txtName;
    public TextField txtEmail;
    public TextField txtRole;
    public Button btnSave;
    public PasswordField txtCurrentPassword;
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public Button btnChangePassword;
    public ImageView imgProfilePic;
    public Button btnBack;

    private UserDTO currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = new UserDTO("U123", "user@example.com", "password123", "User", "John Doe");
        txtUserId.setText(currentUser.getUserId());
        txtEmail.setText(currentUser.getEmail());
        txtName.setText(currentUser.getName());
        txtRole.setText(currentUser.getRole());

        lblUserRole.setText(currentUser.getRole());
        lblUsername.setText(currentUser.getName());
    }

    public void changeProfilePic(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            File selectedFile = fileChooser.showOpenDialog(btnChangePic.getScene().getWindow());
            if (selectedFile != null) {
                Path directoryPath = Paths.get("profile_pics");
                Files.createDirectories(directoryPath);

                String destinationPath = directoryPath + "/" + txtUserId.getText() + ".png";
                Files.copy(selectedFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);

                imgProfilePic.setImage(new Image(Paths.get(destinationPath).toUri().toString()));
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile picture updated successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile picture: " + e.getMessage());
        }
    }

    public void saveProfile(ActionEvent actionEvent) {
        currentUser.setEmail(txtEmail.getText());
        currentUser.setName(txtName.getText());
        currentUser.setRole(txtRole.getText());

        lblUsername.setText(currentUser.getName());
        lblUserRole.setText(currentUser.getRole());

        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
    }

    public void changePassword(ActionEvent actionEvent) {
        String currentPass = txtCurrentPassword.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        if (!currentPass.equals(currentUser.getPassword())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Current password is incorrect.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Error", "New password and confirm password do not match.");
            return;
        }

        currentUser.setPassword(newPass);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully.");

        txtCurrentPassword.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/lk/ijse/hotelmanagementsystem/view/dashboard.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the dashboard: " + e.getMessage());
        }
    }
}

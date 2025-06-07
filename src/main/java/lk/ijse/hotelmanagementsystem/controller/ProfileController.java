package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    public Button btnChangePic;
    @FXML
    public Label lblUsername;
    @FXML
    public Label lblUserRole;
    @FXML
    public TextField txtUserId;
    @FXML
    public TextField txtName;
    @FXML
    public TextField txtEmail;
    @FXML
    public TextField txtRole;
    @FXML
    public Button btnSave;
    @FXML
    public PasswordField txtCurrentPassword;
    @FXML
    public PasswordField txtNewPassword;
    @FXML
    public PasswordField txtConfirmPassword;
    @FXML
    public Button btnChangePassword;
    @FXML
    public ImageView imgProfilePic;
    @FXML
    public Button btnBack;

    private UserDTO currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = new UserDTO("U004", "admin1","paboda4002@gmail.com", "1234", "Admin");
        txtUserId.setText(currentUser.getUserId());
        txtEmail.setText(currentUser.getEmail());
        txtName.setText(currentUser.getName());
        txtRole.setText(currentUser.getRole());
        lblUsername.setText(currentUser.getName());
        lblUserRole.setText(currentUser.getRole());
        txtUserId.setEditable(false);
        txtRole.setEditable(false);

        Path profilePicPath = Paths.get("profile_pics/" + currentUser.getUserId() + ".png");
        if (Files.exists(profilePicPath)) {
            imgProfilePic.setImage(new Image(profilePicPath.toUri().toString()));
        }
    }

    @FXML
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
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile picture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void saveProfile(ActionEvent actionEvent) {
        String email = txtEmail.getText().trim();
        String name = txtName.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name cannot be empty.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return;
        }

        currentUser.setEmail(email);
        currentUser.setName(name);

        lblUsername.setText(currentUser.getName());
        lblUserRole.setText(currentUser.getRole());

        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
    }

    @FXML
    public void changePassword(ActionEvent actionEvent) {
        String currentPass = txtCurrentPassword.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All password fields must be filled.");
            return;
        }

        if (!currentPass.equals(currentUser.getPassword())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Current password is incorrect.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.ERROR, "Error", "New password and confirm password do not match.");
            return;
        }

        if (newPass.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "New password must be at least 6 characters long.");
            return;
        }

        currentUser.setPassword(newPass);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully.");

        txtCurrentPassword.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    @FXML
    public void btnBackOnAction(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/lk/ijse/hotelmanagementsystem/view/Dashboard.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}
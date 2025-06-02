package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.GuestDTO;
import lk.ijse.hotelmanagementsystem.model.GuestModel;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class GuestController implements Initializable {
    public Label lblGuestId;
    public TextField txtName;
    public DatePicker dpDOB;
    public TextField txtEmail;
    public TextField txtContact;
    public TextField txtAddress;
    public DatePicker dpRegistrationDate;
    public ComboBox cmLoyaltyStatus;
    public Button btnCancel;
    public Button btnSave;

    private GuestModel guestModel = new GuestModel();
    private GuestTableController guestTableController;
    private boolean isEditMode = false;
    private String existingGuestId = null;
    private String guestIdValidation = "^G\\d{3}$";
    private String emailValidation = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private String contactValidation = "^\\d{10}$";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void btnCancelOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel and reset the form?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            resetPage();
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        try {
            String guestId = lblGuestId.getText();
            String name = txtName.getText();
            java.time.LocalDate dobLocal = dpDOB.getValue();
            String email = txtEmail.getText();
            String contact = txtContact.getText();
            String address = txtAddress.getText();
            java.time.LocalDate registrationDateLocal = dpRegistrationDate.getValue();
            String loyaltyStatus = cmLoyaltyStatus.getSelectionModel().getSelectedItem().toString();

            if (guestId.isEmpty() || name.isEmpty() || dobLocal == null || email.isEmpty() ||
                    contact.isEmpty() || address.isEmpty() || registrationDateLocal == null || loyaltyStatus == null) {
                new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
                return;
            }

            if (!guestId.matches(guestIdValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid Guest ID format (e.g., G001)").show();
                return;
            }

            if (!email.matches(emailValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid email format").show();
                return;
            }

            if (!contact.matches(contactValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid contact number (must be 10 digits)").show();
                return;
            }

            Date dob = dateFormat.parse(dobLocal.toString());
            Date registrationDate = dateFormat.parse(registrationDateLocal.toString());

            Date today = new Date();
            long ageInMillis = today.getTime() - dob.getTime();
            long ageInYears = ageInMillis / (1000L * 60 * 60 * 24 * 365);
            if (ageInYears < 18) {
                new Alert(Alert.AlertType.ERROR, "❌ Guest must be at least 18 years old").show();
                return;
            }

            GuestDTO guestDTO = new GuestDTO(
                    guestId,
                    name,
                    (java.sql.Date) dob,
                    address,
                    contact,
                    email,
                    (java.sql.Date) registrationDate,
                    loyaltyStatus
            );

            if (isEditMode) {
                boolean isUpdated = guestModel.updateGuest(guestDTO);
                if (isUpdated) {
                    new Alert(Alert.AlertType.INFORMATION, "✅ Guest updated successfully").show();
                    if (guestTableController != null) {
                        guestTableController.loadGuestData();
                    }
                    Stage stage = (Stage) btnSave.getScene().getWindow();
                    stage.close();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed to update guest").show();
                }
            } else {
                boolean isSaved = guestModel.saveGuest(guestDTO);
                if (isSaved) {
                    resetPage();
                    new Alert(Alert.AlertType.INFORMATION, "✅ Guest saved successfully").show();
                    if (guestTableController != null) {
                        guestTableController.loadGuestData();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Guest saving failed").show();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Error saving guest: " + e.getMessage()).show();
        }
    }

    private void resetPage() {
        lblGuestId.setText(GuestModel.getNextGuestId());
        txtName.clear();
        dpDOB.setValue(null);
        txtEmail.clear();
        txtContact.clear();
        txtAddress.clear();
        dpRegistrationDate.setValue(java.time.LocalDate.now());
        cmLoyaltyStatus.getSelectionModel().clearSelection();
        btnSave.setDisable(false);
        btnCancel.setDisable(false);
        isEditMode = false;
        existingGuestId = null;
        lblGuestId.setDisable(false);
    }

    public void setGuestTableController(GuestTableController guestTableController) {
    }

    public void setGuestData(GuestDTO guestDTO) {
        isEditMode = true;
        existingGuestId = guestDTO.getGuestId();
        if (guestDTO != null) {
            lblGuestId.setText(guestDTO.getGuestId());
            txtName.setText(guestDTO.getName());
            // Convert Date to LocalDate for DatePicker
            if (guestDTO.getDob() != null) {
                dpDOB.setValue(java.time.LocalDate.parse(dateFormat.format(guestDTO.getDob())));
            }
            txtEmail.setText(guestDTO.getEmail());
            txtContact.setText(guestDTO.getContact());
            txtAddress.setText(guestDTO.getAddress());
            if (guestDTO.getRegistrationDate() != null) {
                dpRegistrationDate.setValue(java.time.LocalDate.parse(dateFormat.format(guestDTO.getRegistrationDate())));
            }
            cmLoyaltyStatus.setValue(guestDTO.getLoyaltyStatus());

            txtName.setDisable(false);
            dpDOB.setDisable(false);
            txtEmail.setDisable(false);
            txtContact.setDisable(false);
            txtAddress.setDisable(false);
            dpRegistrationDate.setDisable(false);
            cmLoyaltyStatus.setDisable(false);
            btnSave.setDisable(false);
            btnCancel.setDisable(false);
            lblGuestId.setDisable(true);
        } else {
            new Alert(Alert.AlertType.ERROR, "❌ No guest data provided").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmLoyaltyStatus.setItems(FXCollections.observableArrayList("Bronze", "Silver", "Gold"));
        dpRegistrationDate.setValue(java.time.LocalDate.now());

        lblGuestId.setText(GuestModel.getNextGuestId());
    }
}

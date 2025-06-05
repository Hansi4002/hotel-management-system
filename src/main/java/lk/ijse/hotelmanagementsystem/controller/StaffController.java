package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.hotelmanagementsystem.dto.StaffDTO;
import lk.ijse.hotelmanagementsystem.model.StaffModel;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class StaffController implements Initializable {
    public ComboBox<String> cmUserId;
    public TextField txtName;
    public TextField txtPosition;
    public TextField txtContact;
    public DatePicker dpHireDate;
    public Button btnCancel;
    public Button btnSave;
    public Label lblStaffId;

    private StaffTableController staffTableController;
    private final StaffModel staffModel = new StaffModel();
    private boolean isEditMode = false;

    private static final String staffIDValidation = "^S\\d{3}$";
    private static final String nameValidation = "^[A-Za-z ]{3,50}$";
    private static final String contactValidation = "^(07[0-9]{8})$";

    public void btnCancelOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel and reset the form?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                resetPage();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "❌ Error resetting form: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }
    private void resetPage() {
        try {
            String nextId = staffModel.getNextStaffId();
            if (nextId == null || !nextId.matches(staffIDValidation)) {
                throw new SQLException("Invalid reservation ID generated: " + nextId);
            }
            lblStaffId.setText(nextId);
            cmUserId.setValue(null);
            txtName.clear();
            txtPosition.clear();
            txtContact.clear();
            dpHireDate.setValue(null);
            isEditMode = false;
            btnSave.setText("Save");
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to reset form: " + e.getMessage()).show();
            lblStaffId.setText("ERROR");
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        try {
            String staffId = lblStaffId.getText();
            String userId = cmUserId.getValue();
            String name = txtName.getText();
            String position = txtPosition.getText();
            String contact = txtContact.getText();
            LocalDate hireDate = dpHireDate.getValue();

            if (staffId.isEmpty() || userId == null || name.isEmpty() || position.isEmpty() || contact.isEmpty() || hireDate == null) {
                new Alert(Alert.AlertType.WARNING, "⚠️ Please fill in all the fields.").show();
                return;
            }

            if (!staffId.matches(staffIDValidation)) {
                new Alert(Alert.AlertType.WARNING, "⚠️ Invalid Staff ID format!").show();
                return;
            }

            if (!name.matches(nameValidation)) {
                new Alert(Alert.AlertType.WARNING, "⚠️ Name should only contain letters and spaces (3–50 characters).").show();
                return;
            }

            if (!contact.matches(contactValidation)) {
                new Alert(Alert.AlertType.WARNING, "⚠️ Contact must be a valid 10-digit phone number.").show();
                return;
            }

            if (hireDate.isAfter(LocalDate.now())) {
                new Alert(Alert.AlertType.WARNING, "⚠️ Hire date cannot be in the future.").show();
                return;
            }

            StaffDTO staffDTO = new StaffDTO(
                    staffId,
                    userId,
                    name,
                    position,
                    contact,
                    Date.valueOf(hireDate)
            );

            boolean success;
            if (isEditMode) {
                success = staffModel.updateStaff(staffDTO);
            } else {
                success = staffModel.saveStaff(staffDTO);
            }

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, isEditMode ? "✅ Staff updated successfully!" : "✅ Staff saved successfully!").show();
                resetPage();
                if (staffTableController != null) {
                    staffTableController.loadStaffData();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "❌ Operation failed.").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Database error: " + e.getMessage()).show();
            e.printStackTrace();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "❌ Unexpected error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }


    public void setStaffData(StaffDTO staffDTO) {
        if (staffDTO == null || staffDTO.getUserId() == null) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid or missing Staff data").show();
            return;
        }

        isEditMode = true;
        lblStaffId.setText(staffDTO.getStaffId());
        cmUserId.getSelectionModel().select(staffDTO.getUserId());
        txtName.setText(staffDTO.getName());
        txtPosition.setText(staffDTO.getPosition());
        txtContact.setText(staffDTO.getContact());

        if (staffDTO.getHireDate() != null) {
            dpHireDate.setValue(staffDTO.getHireDate().toLocalDate());
        }

        btnSave.setText("Update");
    }

    public void setStaffTableController(StaffTableController staffTableController) {
        this.staffTableController = staffTableController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cmUserId.setItems(FXCollections.observableArrayList(staffModel.getAllUserIds()));

            String nextStaffId = StaffModel.getNextStaffId();
            lblStaffId.setText(nextStaffId);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load user IDs: " + e.getMessage()).show();
        }

        dpHireDate.setValue(LocalDate.now());
    }
}

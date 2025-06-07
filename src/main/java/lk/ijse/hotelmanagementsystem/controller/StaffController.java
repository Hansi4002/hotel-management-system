package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
    private static final String nameValidation = "^[A-Za-z ]{3,50}$";
    private static final String contactValidation = "^07[0-9]{8}$";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) {
            new Alert(Alert.AlertType.ERROR, "❌ StaffView.fxml not found").show();
            return;
        }

        try {
            cmUserId.setItems(FXCollections.observableArrayList(staffModel.getAllUserIds()));
            if (cmUserId.getItems().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "⚠ No user IDs available. Add users first.").show();
            }
            lblStaffId.setText(staffModel.getNextStaffId());
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to initialize form: " + e.getMessage()).show();
            e.printStackTrace();
        }

        dpHireDate.setValue(LocalDate.now().minusDays(1));
    }

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

    public void btnSaveOnAction(ActionEvent actionEvent) {
        try {
            String staffId = lblStaffId.getText();
            String userId = cmUserId.getValue();
            String name = txtName.getText().trim();
            String position = txtPosition.getText().trim();
            String contact = txtContact.getText().trim();
            LocalDate hireDate = dpHireDate.getValue();

            if (staffId.isEmpty() || userId == null || name.isEmpty() || position.isEmpty() || contact.isEmpty() || hireDate == null) {
                new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all fields").show();
                return;
            }

            if (!name.matches(nameValidation)) {
                new Alert(Alert.AlertType.WARNING, "⚠ Name must be 3–50 letters/spaces").show();
                return;
            }

            if (!contact.matches(contactValidation)) {
                new Alert(Alert.AlertType.WARNING, "⚠ Contact must be a 10-digit number (e.g., 0712345678)").show();
                return;
            }

            if (!hireDate.isBefore(LocalDate.now())) {
                new Alert(Alert.AlertType.WARNING, "⚠ Hire date must be before today").show();
                return;
            }

            StaffDTO staffDTO = new StaffDTO(staffId, userId, name, position, contact, Date.valueOf(hireDate));

            boolean success;
            if (isEditMode) {
                success = staffModel.updateStaff(staffDTO);
            } else {
                success = staffModel.saveStaff(staffDTO);
            }

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, isEditMode ? "✅ Staff updated successfully" : "✅ Staff saved successfully").show();
                if (staffTableController != null) {
                    staffTableController.loadStaffData();
                }
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "❌ Failed to save/update staff").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Database error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void setStaffData(StaffDTO staffDTO) {
        if (staffDTO == null) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid staff data").show();
            return;
        }

        isEditMode = true;
        lblStaffId.setText(staffDTO.getStaffId());
        cmUserId.getSelectionModel().select(staffDTO.getUserId());
        txtName.setText(staffDTO.getName());
        txtPosition.setText(staffDTO.getPosition());
        txtContact.setText(staffDTO.getContact());
        dpHireDate.setValue(staffDTO.getHireDate() != null ? staffDTO.getHireDate().toLocalDate() : null);
        btnSave.setText("Update");
        lblStaffId.setDisable(true);
    }

    public void setStaffTableController(StaffTableController controller) {
        this.staffTableController = controller;
    }

    private void resetPage() throws SQLException {
        lblStaffId.setText(staffModel.getNextStaffId());
        cmUserId.getSelectionModel().clearSelection();
        txtName.clear();
        txtPosition.clear();
        txtContact.clear();
        dpHireDate.setValue(LocalDate.now().minusDays(1));
        isEditMode = false;
        btnSave.setText("Save");
        lblStaffId.setDisable(false);
    }
}
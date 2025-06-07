package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.hotelmanagementsystem.dto.MaintenanceDTO;
import lk.ijse.hotelmanagementsystem.model.MaintenanceModel;

import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MaintenanceController implements Initializable {
    public TextField txtDescription;
    public ComboBox<String> cmStaffId;
    public ComboBox<String> cmRoomId;
    public ComboBox<String> cmStatus;
    public Button btnCancel;
    public Button btnSave;

    private boolean isEditMode = false;
    private String currentMaintenanceId;
    private MaintenanceTableController maintenanceTableController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) {
            new Alert(Alert.AlertType.ERROR, "❌ MaintenanceView.fxml not found").show();
            return;
        }
        try {
            List<String> staffIds = MaintenanceModel.getAllStaffIds();
            List<String> roomIds = MaintenanceModel.getAllRoomIds();

            if (staffIds.isEmpty() || roomIds.isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "❌ No staff or rooms available. Please add data to Staff and Room tables.").show();
                btnSave.setDisable(true);
                return;
            }

            cmStaffId.setItems(FXCollections.observableArrayList(staffIds));
            cmRoomId.setItems(FXCollections.observableArrayList(roomIds));
            cmStatus.setItems(FXCollections.observableArrayList("Pending", "In Progress", "Completed"));

            cmStaffId.getSelectionModel().selectFirst();
            cmRoomId.getSelectionModel().selectFirst();
            cmStatus.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load combo box data: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel and reset the form?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            resetPage();
        }
    }

    private void resetPage() {
        txtDescription.clear();
        if (!cmStaffId.getItems().isEmpty()) cmStaffId.getSelectionModel().selectFirst();
        if (!cmRoomId.getItems().isEmpty()) cmRoomId.getSelectionModel().selectFirst();
        if (!cmStatus.getItems().isEmpty()) cmStatus.getSelectionModel().selectFirst();
        isEditMode = false;
        currentMaintenanceId = null;
        btnSave.setDisable(false);
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        String description = txtDescription.getText().trim();
        String staffId = cmStaffId.getValue();
        String roomId = cmRoomId.getValue();
        String status = cmStatus.getValue();

        if (description.isEmpty() || staffId == null || roomId == null || status == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
            return;
        }

        try {
            List<String> validStaffIds = MaintenanceModel.getAllStaffIds();
            List<String> validRoomIds = MaintenanceModel.getAllRoomIds();

            if (!validStaffIds.contains(staffId)) {
                new Alert(Alert.AlertType.WARNING, "⚠ Invalid Staff ID selected").show();
                return;
            }
            if (!validRoomIds.contains(roomId)) {
                new Alert(Alert.AlertType.WARNING, "⚠ Invalid Room ID selected").show();
                return;
            }

            String maintenanceId = isEditMode ? currentMaintenanceId : MaintenanceModel.generateMaintenanceId();

            MaintenanceDTO dto = new MaintenanceDTO(maintenanceId, description, staffId, roomId, status);
            boolean success = isEditMode ? MaintenanceModel.updateMaintenance(dto) : MaintenanceModel.saveMaintenance(dto);

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, isEditMode ?
                        "✅ Maintenance record updated successfully" : "✅ Maintenance record saved successfully").show();
                resetPage();
                if (maintenanceTableController != null) {
                    maintenanceTableController.loadMaintenanceData();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, isEditMode ?
                        "❌ Failed to update maintenance record" : "❌ Failed to save maintenance record").show();
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Foreign key error: Ensure Room ID and Staff ID exist in the database. " + e.getMessage()).show();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Database error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void setMaintenanceController(MaintenanceTableController maintenanceTableController) {
        this.maintenanceTableController = maintenanceTableController;
    }

    public void setMaintenanceData(MaintenanceDTO dto) {
        if (dto == null || dto.getMaintenanceId() == null) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid or missing Maintenance data").show();
            return;
        }

        isEditMode = true;
        currentMaintenanceId = dto.getMaintenanceId();
        txtDescription.setText(dto.getDescription());
        cmStaffId.getSelectionModel().select(dto.getStaffId());
        cmRoomId.getSelectionModel().select(dto.getRoomId());
        cmStatus.getSelectionModel().select(dto.getStatus());
    }
}
package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.hotelmanagementsystem.dto.MaintenanceDTO;
import lk.ijse.hotelmanagementsystem.model.MaintenanceModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class MaintenanceController implements Initializable {
    public TextField txtDescription;
    public ComboBox cmStaffId;
    public ComboBox cmRoomId;
    public TextField txtMaintenanceId;
    public ComboBox cmStatus;
    public Button btnCancel;
    public Button btnSave;

    private boolean isEditMode = false;
    private MaintenanceTableController maintenanceTableController;

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
        txtMaintenanceId.clear();
        txtDescription.clear();
        cmStaffId.getSelectionModel().clearSelection();
        cmRoomId.getSelectionModel().clearSelection();
        cmStatus.getSelectionModel().clearSelection();
        isEditMode = false;
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws SQLException {
        String maintenanceId = txtMaintenanceId.getText();
        String description = txtDescription.getText();
        String staffId = cmStaffId.getSelectionModel().getSelectedItem().toString();
        String roomId = cmRoomId.getSelectionModel().getSelectedItem().toString();
        String status = cmStatus.getSelectionModel().getSelectedItem().toString();

        if (maintenanceId.isEmpty() || description.isEmpty() || staffId == null || roomId == null || status == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
            return;
        }

        MaintenanceDTO dto = new MaintenanceDTO(maintenanceId, description, staffId, roomId, status);
        boolean success;
        if (isEditMode) {
            success = MaintenanceModel.updateMaintenance(dto);
        } else {
            success = MaintenanceModel.saveMaintenance(dto);
        }
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
        txtMaintenanceId.setText(dto.getMaintenanceId());
        txtDescription.setText(dto.getDescription());
        cmStaffId.getSelectionModel().select(dto.getStaffId());
        cmRoomId.getSelectionModel().select(dto.getRoomId());
        cmStatus.getSelectionModel().select(dto.getStatus());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cmStaffId.setItems(FXCollections.observableArrayList(MaintenanceModel.getAllStaffIds()));
            cmRoomId.setItems(FXCollections.observableArrayList(MaintenanceModel.getAllRoomIds()));
            cmStatus.setItems(FXCollections.observableArrayList("Pending", "In Progress", "Completed"));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load combo box data: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }
}

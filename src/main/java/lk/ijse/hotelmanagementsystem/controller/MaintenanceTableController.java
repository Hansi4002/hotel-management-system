package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.MaintenanceDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.MaintenanceTM;
import lk.ijse.hotelmanagementsystem.model.MaintenanceModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MaintenanceTableController implements Initializable {
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddFoodOrder;
    public TableView<MaintenanceTM> tblMaintenance;
    public TableColumn<MaintenanceTM, String> colMaintenanceId;
    public TableColumn<MaintenanceTM, String> colRoomId;
    public TableColumn<MaintenanceTM, String> colStaffId;
    public TableColumn<MaintenanceTM, String> colDescription;
    public TableColumn<MaintenanceTM, String> colStatus;

    private final MaintenanceModel maintenanceModel = new MaintenanceModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colMaintenanceId.setCellValueFactory(new PropertyValueFactory<>("maintenanceId"));
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colStaffId.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblMaintenance.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        try {
            loadMaintenanceData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading maintenance data: " + e.getMessage());
        }
    }

    public void loadMaintenanceData() throws SQLException {
        List<MaintenanceTM> maintenanceList = maintenanceModel.getAllMaintenance();
        if (maintenanceList == null) {
            maintenanceList = Collections.emptyList();
        }

        List<MaintenanceTM> maintenanceTMs = maintenanceList.stream()
                .map(dto -> new MaintenanceTM(
                        dto.getMaintenanceId(),
                        dto.getRoomId(),
                        dto.getStaffId(),
                        dto.getDescription(),
                        dto.getStatus()
                ))
                .collect(Collectors.toList());

        ObservableList<MaintenanceTM> obList = FXCollections.observableArrayList(maintenanceTMs);
        tblMaintenance.setItems(obList);
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        MaintenanceTM selectedMaintenance = tblMaintenance.getSelectionModel().getSelectedItem();

        if (selectedMaintenance == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a maintenance record to edit");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MaintenanceView.fxml"));
            Parent load = loader.load();

            MaintenanceController controller = loader.getController();
            controller.setMaintenanceData(new MaintenanceDTO(
                    selectedMaintenance.getMaintenanceId(),
                    selectedMaintenance.getRoomId(),
                    selectedMaintenance.getStaffId(),
                    selectedMaintenance.getDescription(),
                    selectedMaintenance.getStatus()
            ));

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);  // block interaction with other windows
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Maintenance Record");
            stage.showAndWait();

            loadMaintenanceData();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to open Edit Maintenance window: " + e.getMessage());
        }
    }

    public void btnAddNewFoodOrderOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MaintenanceView.fxml"));
            Parent load = loader.load();

            MaintenanceController controller = loader.getController();
            controller.setMaintenanceController(this);  // Make sure this method exists!

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Maintenance Record");
            stage.showAndWait();

            loadMaintenanceData();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to open Add Maintenance Record: " + e.getMessage());
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        MaintenanceTM selectedMaintenance = tblMaintenance.getSelectionModel().getSelectedItem();

        if (selectedMaintenance == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a maintenance record to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete maintenance record " + selectedMaintenance.getMaintenanceId() + "?",
                ButtonType.YES, ButtonType.NO);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean isDeleted = false;
                try {
                    isDeleted = maintenanceModel.deleteMaintenance(selectedMaintenance.getMaintenanceId());
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Error deleting maintenance record: " + e.getMessage());
                    return;
                }
                if (isDeleted) {
                    tblMaintenance.getItems().remove(selectedMaintenance);
                    showAlert(Alert.AlertType.INFORMATION, "✅ Maintenance record deleted successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "❌ Failed to delete maintenance record");
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}

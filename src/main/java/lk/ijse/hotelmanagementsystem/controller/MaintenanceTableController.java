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

public class MaintenanceTableController implements Initializable {
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddMaintenance;
    public TableView<MaintenanceTM> tblMaintenance;
    public TableColumn<MaintenanceTM, String> colMaintenanceId;
    public TableColumn<MaintenanceTM, String> colRoomId;
    public TableColumn<MaintenanceTM, String> colStaffId;
    public TableColumn<MaintenanceTM, String> colDescription;
    public TableColumn<MaintenanceTM, String> colStatus;
    public TextField txtSearch;
    public Button btnSearch;

    private final MaintenanceModel maintenanceModel;
    private Stage activeStage;

    public MaintenanceTableController() {
        this.maintenanceModel = new MaintenanceModel();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) {
            showAlert(Alert.AlertType.ERROR, "❌ MaintenanceTableView.fxml not found");
            return;
        }

        if (tblMaintenance == null || colMaintenanceId == null || colRoomId == null ||
                colStaffId == null || colDescription == null || colStatus == null ||
                btnAddMaintenance == null || btnEdit == null || btnCancel == null ||
                txtSearch == null || btnSearch == null) {
            showAlert(Alert.AlertType.ERROR, "❌ One or more UI components are not properly bound in FXML");
            return;
        }

        colMaintenanceId.setCellValueFactory(new PropertyValueFactory<>("maintenanceId"));
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colStaffId.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblMaintenance.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblMaintenance.setSortPolicy(param -> true);

        try {
            loadMaintenanceData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading maintenance data: " + e.getMessage());
        }
    }

    public void loadMaintenanceData() throws SQLException {
        List<MaintenanceTM> maintenanceList = maintenanceModel.getAllMaintenance();
        ObservableList<MaintenanceTM> obList = FXCollections.observableArrayList(maintenanceList != null ? maintenanceList : Collections.emptyList());
        tblMaintenance.setItems(obList);
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        MaintenanceTM selectedMaintenance = tblMaintenance.getSelectionModel().getSelectedItem();

        if (selectedMaintenance == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a maintenance record to edit");
            return;
        }

        if (activeStage != null && activeStage.isShowing()) {
            activeStage.toFront();
            return;
        }

        try {
            URL fxmlLocation = getClass().getResource("/view/MaintenanceView.fxml");
            if (fxmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "❌ MaintenanceView.fxml not found");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent load = loader.load();

            MaintenanceController controller = loader.getController();
            controller.setMaintenanceData(new MaintenanceDTO(
                    selectedMaintenance.getMaintenanceId(),
                    selectedMaintenance.getDescription(),
                    selectedMaintenance.getStaffId(),
                    selectedMaintenance.getRoomId(),
                    selectedMaintenance.getStatus()
            ));

            activeStage = new Stage();
            activeStage.initModality(Modality.APPLICATION_MODAL);
            activeStage.setScene(new Scene(load));
            activeStage.setTitle("Edit Maintenance Record");
            activeStage.setOnCloseRequest(e -> activeStage = null);
            activeStage.showAndWait();

            loadMaintenanceData();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to open Edit Maintenance window: " + e.getMessage());
        }
    }

    public void btnAddMaintenanceOnAction(ActionEvent actionEvent) {
        if (activeStage != null && activeStage.isShowing()) {
            activeStage.toFront();
            return;
        }

        try {
            URL fxmlLocation = getClass().getResource("/view/MaintenanceView.fxml");
            if (fxmlLocation == null) {
                showAlert(Alert.AlertType.ERROR, "❌ MaintenanceView.fxml not found");
                return;
            }
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent load = loader.load();

            MaintenanceController controller = loader.getController();
            controller.setMaintenanceController(this);

            activeStage = new Stage();
            activeStage.initModality(Modality.APPLICATION_MODAL);
            activeStage.setScene(new Scene(load));
            activeStage.setTitle("Add New Maintenance Record");
            activeStage.setOnCloseRequest(e -> activeStage = null);
            activeStage.showAndWait();

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

    public void btnSearchOnAction(ActionEvent actionEvent) {
        String query = txtSearch.getText().trim().toLowerCase();

        try {
            if (query.isEmpty()) {
                loadMaintenanceData();
                return;
            }

            ObservableList<MaintenanceTM> allItems = tblMaintenance.getItems();
            ObservableList<MaintenanceTM> filteredItems = FXCollections.observableArrayList();

            for (MaintenanceTM item : allItems) {
                if (item.getMaintenanceId().toLowerCase().contains(query) ||
                        item.getRoomId().toLowerCase().contains(query) ||
                        item.getStaffId().toLowerCase().contains(query) ||
                        item.getDescription().toLowerCase().contains(query) ||
                        item.getStatus().toLowerCase().contains(query)) {
                    filteredItems.add(item);
                }
            }

            if (filteredItems.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No maintenance records match your search criteria");
                loadMaintenanceData();
            } else {
                tblMaintenance.setItems(filteredItems);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error searching maintenance records: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }
}
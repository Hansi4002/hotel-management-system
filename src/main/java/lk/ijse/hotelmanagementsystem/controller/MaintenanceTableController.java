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
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.MaintenanceDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.MaintenanceTM;
import lk.ijse.hotelmanagementsystem.model.MaintenanceModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MaintenanceTableController implements Initializable {
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddFoodOrder;
    public TableView<MaintenanceTM> tblMaintenance;
    public TableColumn<MaintenanceTM,String> colMaintenanceId;
    public TableColumn<MaintenanceTM,String> colRoomId;
    public TableColumn<MaintenanceTM,String> colStaffId;
    public TableColumn<MaintenanceTM,String> colDescription;
    public TableColumn<MaintenanceTM,String> colStatus;

    private MaintenanceModel maintenanceModel = new MaintenanceModel();

    public void btnEditOnAction(ActionEvent actionEvent) {
        MaintenanceTM selectedMaintenance = tblMaintenance.getSelectionModel().getSelectedItem();

        if (selectedMaintenance == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a maintenance record to edit").show();
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
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Maintenance Record");
            stage.show();

            stage.setOnHiding(event -> {
                try {
                    loadMaintenanceData();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Edit Maintenance window").show();
        }
    }

    public void btnAddNewFoodOrderOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MaintenanceView.fxml"));
            Parent load = loader.load();
            MaintenanceController controller = loader.getController();
            controller.setMaintenanceController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Maintenance Record");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Add Maintenance Record").show();
        }
    }

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
            throw new RuntimeException(e);
        }
    }

    public void loadMaintenanceData() throws SQLException {
        List<MaintenanceTM> maintenanceList = maintenanceModel.getAllMaintenance();
        List<MaintenanceTM> maintenanceTMs = maintenanceList.stream().map(dto -> new MaintenanceTM(
                dto.getMaintenanceId(),
                dto.getRoomId(),
                dto.getStaffId(),
                dto.getDescription(),
                dto.getStatus()
        )).toList();

        ObservableList<MaintenanceTM> obList = FXCollections.observableArrayList(maintenanceTMs);
        tblMaintenance.setItems(obList);
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        MaintenanceTM selectedMaintenance = tblMaintenance.getSelectionModel().getSelectedItem();

        if (selectedMaintenance == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a maintenance record to delete").show();
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
                    throw new RuntimeException(e);
                }
                if (isDeleted) {
                    tblMaintenance.getItems().remove(selectedMaintenance);
                    new Alert(Alert.AlertType.INFORMATION, "✅ Maintenance record deleted successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed to delete maintenance record").show();
                }
            }
        });
    }
}

package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.StaffDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.StaffTM;
import lk.ijse.hotelmanagementsystem.model.StaffModel;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class StaffTableController implements Initializable {
    public Button btnDelete;
    public Button btnEdit;
    public Button btnAddStaff;
    public TableView<StaffTM> tblStaff;
    public TableColumn<StaffTM, String> colStaffId;
    public TableColumn<StaffTM, String> colUserId;
    public TableColumn<StaffTM, String> colName;
    public TableColumn<StaffTM, String> colPosition;
    public TableColumn<StaffTM, String> colContact;
    public TableColumn<StaffTM, Date> colHireDate;
    public TextField txtSearch;
    public Button btnSearch;

    private final StaffModel staffModel = new StaffModel();
    public Button btnCancel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url == null) {
            new Alert(Alert.AlertType.ERROR, "❌ StaffTableView.fxml not found").show();
            return;
        }

        colStaffId.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));

        tblStaff.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        try {
            loadStaffData();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load staff data: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void loadStaffData() throws SQLException {
        List<StaffDTO> staffList = staffModel.getAllStaff();
        List<StaffTM> tmList = staffList.stream().map(dto -> new StaffTM(
                dto.getStaffId(),
                dto.getUserId(),
                dto.getName(),
                dto.getPosition(),
                dto.getContact(),
                dto.getHireDate()
        )).toList();

        tblStaff.setItems(FXCollections.observableArrayList(tmList));
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        StaffTM selected = tblStaff.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a staff member to delete").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete staff member '" + selected.getName() + "'?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean deleted = staffModel.deleteStaff(selected.getStaffId());
                    if (deleted) {
                        tblStaff.getItems().remove(selected);
                        new Alert(Alert.AlertType.INFORMATION, "✅ Staff member deleted").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "❌ Failed to delete staff member").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, "❌ Deletion error: " + e.getMessage()).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        StaffTM selected = tblStaff.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a staff member to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StaffView.fxml"));
            Parent load = loader.load();
            StaffController controller = loader.getController();
            controller.setStaffData(new StaffDTO(
                    selected.getStaffId(),
                    selected.getUserId(),
                    selected.getName(),
                    selected.getPosition(),
                    selected.getContact(),
                    selected.getHireDate()
            ));
            controller.setStaffTableController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Staff");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadStaffData();
        } catch (IOException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open edit staff window: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnAddNewStaffOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StaffView.fxml"));
            Parent load = loader.load();
            StaffController controller = loader.getController();
            controller.setStaffTableController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Staff");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadStaffData();
        } catch (IOException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open add staff window: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            try {
                loadStaffData();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Failed to load staff data: " + e.getMessage()).show();
                e.printStackTrace();
            }
            return;
        }

        try {
            List<StaffDTO> staffList = staffModel.getAllStaff();
            List<StaffTM> filteredList = staffList.stream()
                    .filter(dto -> dto.getStaffId().toLowerCase().contains(searchText) ||
                            dto.getName().toLowerCase().contains(searchText) ||
                            dto.getPosition().toLowerCase().contains(searchText))
                    .map(dto -> new StaffTM(
                            dto.getStaffId(),
                            dto.getUserId(),
                            dto.getName(),
                            dto.getPosition(),
                            dto.getContact(),
                            dto.getHireDate()
                    )).toList();

            tblStaff.setItems(FXCollections.observableArrayList(filteredList));
            if (filteredList.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "ℹ No staff found for: " + searchText).show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Search error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        tblStaff.getSelectionModel().clearSelection();
    }
}
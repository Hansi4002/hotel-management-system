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
import lk.ijse.hotelmanagementsystem.dto.StaffDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.StaffTM;
import lk.ijse.hotelmanagementsystem.model.StaffModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class StaffTableController implements Initializable {
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddStaff;
    public TableView<StaffTM> tblStaff;
    public TableColumn<StaffTM,String> colStaffId;
    public TableColumn<StaffTM,String> colUserId;
    public TableColumn<StaffTM,String> colName;
    public TableColumn<StaffTM,String> colPosition;
    public TableColumn<StaffTM,java.sql.Date> colContact;
    public TableColumn<StaffTM,String> colHireDate;

    private final StaffModel staffModel = new StaffModel();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
            throw new RuntimeException(e);
        }
    }

    public void loadStaffData() throws SQLException {
        List<StaffDTO> staffList = StaffModel.getAllStaff();
        List<StaffTM> tmList = staffList.stream().map(dto -> new StaffTM(
                dto.getStaffId(),
                dto.getUserId(),
                dto.getName(),
                dto.getPosition(),
                dto.getContact(),
                dto.getHireDate()
        )).toList();

        ObservableList<StaffTM> observableList = FXCollections.observableArrayList(tmList);
        tblStaff.setItems(observableList);
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        StaffTM selected = tblStaff.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a staff member to delete").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this staff member?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean deleted = staffModel.deleteStaff(selected.getStaffId());
                    if (deleted) {
                        tblStaff.getItems().remove(selected);
                        new Alert(Alert.AlertType.INFORMATION, "✅ Staff member deleted successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "❌ Failed to delete staff member").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "❌ An error occurred while deleting the staff member").show();
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

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Staff");
            stage.show();

            stage.setOnHiding(event -> {
                try {
                    loadStaffData();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open edit staff window").show();
        }
    }


    public void btnAddNewStaffOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StaffView.fxml"));
            Parent root = loader.load();

            StaffController controller = loader.getController();
            controller.setStaffTableController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add New Staff");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadStaffData();

        } catch (IOException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Add Staff window: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }
}

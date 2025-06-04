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
import lk.ijse.hotelmanagementsystem.dto.SupplierDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.SupplierTM;
import lk.ijse.hotelmanagementsystem.model.SupplierModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class SupplierTableController implements Initializable {
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddSupplier;
    public TableView<SupplierTM> tblSupplier;
    public TableColumn<SupplierTM,String> colSupplierId;
    public TableColumn<SupplierTM,String> colName;
    public TableColumn<SupplierTM,String> colContact;
    public TableColumn<SupplierTM,String> colEmail;
    public TableColumn<SupplierTM,String> colAddress;

    private final SupplierModel supplierModel = new SupplierModel();

    public void btnCancelOnAction(ActionEvent actionEvent) {
        SupplierTM selectedSupplier = tblSupplier.getSelectionModel().getSelectedItem();

        if (selectedSupplier == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a supplier to delete").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete supplier " + selectedSupplier.getName() + " (" + selectedSupplier.getSupplierId() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean isDeleted = supplierModel.deleteSupplier(selectedSupplier.getSupplierId());
                    if (isDeleted) {
                        loadSupplierData();
                        new Alert(Alert.AlertType.INFORMATION, "✅ Supplier deleted successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "❌ Failed to delete supplier").show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "❌ Error deleting supplier: " + e.getMessage()).show();
                }
            }
        });
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        SupplierTM selectedSupplier = tblSupplier.getSelectionModel().getSelectedItem();
        if (selectedSupplier == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a supplier to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SupplierView.fxml"));
            Parent load = loader.load();

            SupplierController controller = loader.getController();
            controller.setSupplierData(new SupplierDTO(
                    selectedSupplier.getSupplierId(),
                    selectedSupplier.getName(),
                    selectedSupplier.getContact(),
                    selectedSupplier.getEmail(),
                    selectedSupplier.getAddress()
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Supplier");
            stage.show();

            stage.setOnHiding(event -> loadSupplierData());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Edit Supplier window: " + e.getMessage());
        }
    }

    public void btnAddNewSupplierOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SupplierView.fxml"));
            Parent load = loader.load();
            SupplierController controller = loader.getController();
            controller.setSupplierTableController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Supplier");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Add Supplier window").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        tblSupplier.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        loadSupplierData();
    }

    public void loadSupplierData() {
        try {
            List<SupplierDTO> supplierList = supplierModel.getAllSuppliers();
            List<SupplierTM> supplierTMS = supplierList.stream().map(dto -> new SupplierTM(
                    dto.getSupplierId(),
                    dto.getName(),
                    dto.getContact(),
                    dto.getEmail(),
                    dto.getAddress()
            )).toList();

            ObservableList<SupplierTM> supplierObservableList = FXCollections.observableList(supplierTMS);
            tblSupplier.setItems(supplierObservableList);
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load food data").show();
        }
    }
}

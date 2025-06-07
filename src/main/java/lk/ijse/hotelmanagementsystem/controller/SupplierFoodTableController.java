package lk.ijse.hotelmanagementsystem.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
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
import lk.ijse.hotelmanagementsystem.dto.SupplierFoodDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.SupplierFoodTM;
import lk.ijse.hotelmanagementsystem.model.SupplierFoodModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class SupplierFoodTableController implements Initializable {
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddSupplierFood;
    public TableView<SupplierFoodTM> tblSupplierFood;
    public TableColumn<SupplierFoodTM,String> colMenuId;
    public TableColumn<SupplierFoodTM,String> colSupplierId;
    public TableColumn<SupplierFoodTM,Double> colCost;
    public TableColumn<SupplierFoodTM,Integer> colQuantity;
    public TableColumn<SupplierFoodTM,String> colSupplyDate;
    public TableColumn<SupplierFoodTM,Double> colTotalCost;

    private final SupplierFoodModel supplierFoodModel = new SupplierFoodModel();
    public TextField txtSearch;
    public Button btnSearch;

    public void btnCancelOnAction(ActionEvent actionEvent) {
        SupplierFoodTM selectedFood = tblSupplierFood.getSelectionModel().getSelectedItem();

        if (selectedFood == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a supplier food item to cancel").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel the supplier food item for Menu ID " +
                        selectedFood.getMenuId() + " and Supplier ID " + selectedFood.getSupplierId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean isDeleted = false;
                try {
                    isDeleted = supplierFoodModel.deleteSupplierFood(
                            selectedFood.getMenuId(), selectedFood.getSupplierId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
                    return;
                }

                if (isDeleted) {
                    tblSupplierFood.getItems().remove(selectedFood);
                    new Alert(Alert.AlertType.INFORMATION, "✅ Supplier food item cancelled successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed to cancel supplier food item").show();
                }
            }
        });
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        SupplierFoodTM selectedFood = tblSupplierFood.getSelectionModel().getSelectedItem();

        if (selectedFood == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a supplier food item to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SupplierFoodView.fxml"));
            Parent load = loader.load();
            SupplierFoodController controller = loader.getController();
            controller.setSupplierFoodTableController(this);
            controller.setSupplierFoodData(new SupplierFoodDTO(
                    selectedFood.getMenuId(),
                    selectedFood.getSupplierId(),
                    selectedFood.getCost(),
                    selectedFood.getQuantity(),
                    selectedFood.getSupplyDate()
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Supplier Food Item");
            stage.show();

            stage.setOnHiding(event -> {
                try {
                    loadSupplierFoodData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
                }
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Edit Supplier Food window: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnAddNewSupplierFoodOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SupplierFoodView.fxml"));
            Parent load = loader.load();
            SupplierFoodController controller = loader.getController();
            controller.setSupplierFoodTableController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Supplier Food Item");
            stage.show();

            stage.setOnHiding(event -> {
                try {
                    loadSupplierFoodData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
                }
            });

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Add Supplier Food Item window: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void loadSupplierFoodData() throws SQLException {
        List<SupplierFoodDTO> supplierFoodList = supplierFoodModel.getAllSupplierFoods();
        List<SupplierFoodTM> supplierFoodTMs = supplierFoodList.stream().map(dto -> new SupplierFoodTM(
                dto.getMenuId(),
                dto.getSupplierId(),
                dto.getCost(),
                dto.getQuantity(),
                dto.getSupplyDate()
        )).toList();

        ObservableList<SupplierFoodTM> observableList = FXCollections.observableArrayList(supplierFoodTMs);
        tblSupplierFood.setItems(observableList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colMenuId.setCellValueFactory(new PropertyValueFactory<>("menuId"));
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSupplyDate.setCellValueFactory(new PropertyValueFactory<>("supplyDate"));
        colTotalCost.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotalCost()));

        tblSupplierFood.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        try {
            loadSupplierFoodData();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
        }
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        String keyword = txtSearch.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            try {
                loadSupplierFoodData();
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).show();
            }
            return;
        }

        ObservableList<SupplierFoodTM> filteredList = FXCollections.observableArrayList();
        for (SupplierFoodTM item : tblSupplierFood.getItems()) {
            if (
                    item.getMenuId().toLowerCase().contains(keyword) ||
                            item.getSupplierId().toLowerCase().contains(keyword)
            ) {
                filteredList.add(item);
            }
        }

        tblSupplierFood.setItems(filteredList);
    }
}

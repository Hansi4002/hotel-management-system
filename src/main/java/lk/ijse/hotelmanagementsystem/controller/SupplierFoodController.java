package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.hotelmanagementsystem.dto.SupplierFoodDTO;
import lk.ijse.hotelmanagementsystem.model.SupplierFoodModel;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class SupplierFoodController implements Initializable {
    public TextField txtQuantity;
    public ComboBox<String> cmMenuId;
    public ComboBox<String> cmSupplierId;
    public TextField txtCost;
    public DatePicker dpSupplyDate;
    public Button btnCancel;
    public Button btnSave;

    private boolean isEditMode = false;
    private SupplierFoodTableController supplierFoodTableController;

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
        cmMenuId.getSelectionModel().clearSelection();
        cmSupplierId.getSelectionModel().clearSelection();
        txtCost.clear();
        txtQuantity.clear();
        dpSupplyDate.setValue(null);
        isEditMode = false;
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws SQLException {
        String menuId = cmMenuId.getSelectionModel().getSelectedItem();
        String supplierId = cmSupplierId.getSelectionModel().getSelectedItem();
        String cost = txtCost.getText();
        String quantity = txtQuantity.getText();
        LocalDate supplyDate = dpSupplyDate.getValue();

        if (menuId == null || supplierId == null || cost.isEmpty() || quantity.isEmpty() || supplyDate == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
            return;
        }

        double costDouble;
        try {
            costDouble = Double.parseDouble(cost);
            if (costDouble <= 0) {
                new Alert(Alert.AlertType.ERROR, "❌ Cost must be greater than zero").show();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid cost format").show();
            return;
        }

        int quantityInt;
        try {
            quantityInt = Integer.parseInt(quantity);
            if (quantityInt <= 0) {
                new Alert(Alert.AlertType.ERROR, "❌ Quantity must be greater than zero").show();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid quantity format").show();
            return;
        }

        Date sqlDate = Date.valueOf(String.valueOf(supplyDate));
        SupplierFoodDTO dto = new SupplierFoodDTO(menuId, supplierId, costDouble, quantityInt, sqlDate);

        boolean success;
        if (isEditMode) {
            success = SupplierFoodModel.updateSupplierFood(dto);
        } else {
            success = SupplierFoodModel.saveSupplierFood(dto);
        }

        if (success) {
            new Alert(Alert.AlertType.INFORMATION, isEditMode ?
                    "✅ Supplier stock updated successfully" : "✅ Supplier stock saved successfully").show();
            resetPage();

            if (supplierFoodTableController != null) {
                supplierFoodTableController.loadSupplierFoodData();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, isEditMode ?
                    "❌ Failed to update supplier stock" : "❌ Failed to save supplier stock").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cmMenuId.setItems(FXCollections.observableArrayList(SupplierFoodModel.getAllMenuIds()));
            cmSupplierId.setItems(FXCollections.observableArrayList(SupplierFoodModel.getAllSupplierIds()));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load combo box data: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void setSupplierFoodData(SupplierFoodDTO dto) {
        if (dto == null || dto.getMenuId() == null || dto.getSupplierId() == null) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid or missing Supplier Food data").show();
            return;
        }

        isEditMode = true;
        cmMenuId.getSelectionModel().select(dto.getMenuId());
        cmSupplierId.getSelectionModel().select(dto.getSupplierId());
        txtCost.setText(String.valueOf(dto.getCost()));
        txtQuantity.setText(String.valueOf(dto.getQuantity()));

        if (dto.getSupplyDate() != null) {
            dpSupplyDate.setValue(dto.getSupplyDate().toLocalDate());
        }
    }

    public void setSupplierFoodTableController(SupplierFoodTableController supplierFoodTableController) {
        this.supplierFoodTableController = supplierFoodTableController;
    }
}

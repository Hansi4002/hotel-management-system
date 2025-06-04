package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.hotelmanagementsystem.dto.FoodOrderDetailDTO;
import lk.ijse.hotelmanagementsystem.model.FoodOrderDetailsModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class FoodOrderDetailController implements Initializable {
    public TextField txtItemPrice;
    public ComboBox<String> cmMenuId;
    public ComboBox<String> cmOrderId;
    public TextField txtQuantity;
    public Button btnCancel;
    public Button btnSave;

    private boolean isEditMode = false;
    private FoodOrderDetailsTableController foodOrderDetailsTableController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cmMenuId.setItems(FXCollections.observableArrayList(FoodOrderDetailsModel.getAllMenuIds()));
            cmOrderId.setItems(FXCollections.observableArrayList(FoodOrderDetailsModel.getAllOrderIds()));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load menu or order IDs: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

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
        cmOrderId.getSelectionModel().clearSelection();
        txtItemPrice.clear();
        txtQuantity.clear();
        isEditMode = false;
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws SQLException {
        String menuId = cmMenuId.getSelectionModel().getSelectedItem();
        String orderId = cmOrderId.getSelectionModel().getSelectedItem();
        String itemPrice = txtItemPrice.getText();
        String quantity = txtQuantity.getText();

        if (menuId == null || orderId == null || itemPrice.isEmpty() || quantity.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
            return;
        }

        double itemPriceDouble;
        try {
            itemPriceDouble = Double.parseDouble(itemPrice);
            if (itemPriceDouble <= 0) {
                new Alert(Alert.AlertType.ERROR, "❌ Item price must be greater than zero").show();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid item price format").show();
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

        FoodOrderDetailDTO orderDetailDTO = new FoodOrderDetailDTO(menuId, orderId, itemPriceDouble, quantityInt);
        boolean success;
        if (isEditMode) {
            success = FoodOrderDetailsModel.updateFoodOrderDetail(orderDetailDTO);
        } else {
            success = FoodOrderDetailsModel.saveFoodOrderDetail(orderDetailDTO);
        }

        if (success) {
            new Alert(Alert.AlertType.INFORMATION, isEditMode ? "✅ Food order detail updated successfully" :
                    "✅ Food order detail saved successfully").show();
            resetPage();
            if (foodOrderDetailsTableController != null) {
                foodOrderDetailsTableController.loadFoodOrderDetailsData();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, isEditMode ? "❌ Failed to update food order detail" :
                    "❌ Failed to save food order detail").show();
        }
    }

    public void setFoodOrderDetailData(FoodOrderDetailDTO dto) {
        if (dto == null || dto.getMenuId() == null || dto.getOrderId() == null) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid or missing Food Order Detail data").show();
            return;
        }

        isEditMode = true;
        cmMenuId.getSelectionModel().select(dto.getMenuId());
        cmOrderId.getSelectionModel().select(dto.getOrderId());
        txtItemPrice.setText(String.valueOf(dto.getItemPrice()));
        txtQuantity.setText(String.valueOf(dto.getQuantity()));
    }

    public void setFoodOrderDetailsTableController(FoodOrderDetailsTableController controller) {
        this.foodOrderDetailsTableController = controller;
    }
}
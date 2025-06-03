package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.hotelmanagementsystem.dto.FoodOrderDTO;
import lk.ijse.hotelmanagementsystem.model.FoodOrderModel;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class FoodOrderController implements Initializable {
    public Label lblOrderId;
    public ComboBox <String> cmReservationId;
    public ComboBox <String> cmOrderType;
    public TextField txtTotalAmount;
    public ComboBox <String> cmStatus;
    public DatePicker dpOrderDate;
    public Button btnCancel;
    public Button btnSave;

    private final String orderIdValidation = "O\\d{3}";
    private boolean isEditMode = false;
    private String existingOrderId = null;
    private FoodOrderTableController foodOrderTableController;

    public void btnCancelOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel and reset the form?",
                ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try{
                resetPage();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,"❌ Error resetting form: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    private void resetPage() {
        try {
            lblOrderId.setText(FoodOrderModel.getNextOrderId());
            cmReservationId.getSelectionModel().clearSelection();
            cmOrderType.getSelectionModel().clearSelection();
            cmStatus.getSelectionModel().clearSelection();
            txtTotalAmount.clear();
            dpOrderDate.setValue(null);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Error resetting form: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        try {
            String orderId = lblOrderId.getText();
            String reservationId = cmReservationId.getSelectionModel().getSelectedItem().toString();
            String orderType = cmOrderType.getSelectionModel().getSelectedItem().toString();
            String status = cmStatus.getSelectionModel().getSelectedItem().toString();
            String totalAmount = txtTotalAmount.getText();
            LocalDate orderDate = dpOrderDate.getValue();

            if (orderId == null || reservationId == null || orderType == null || status == null ||
                    totalAmount.isEmpty() || orderDate == null) {
                new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
                return;
            }

            if (!orderId.matches(orderIdValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid Order ID format (e.g., O001)").show();
                return;
            }

            double total;
            try {
                total = Double.parseDouble(totalAmount);
                if (total <= 0) {
                    new Alert(Alert.AlertType.ERROR, "❌ Total amount must be greater than zero").show();
                    return;
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid total amount format").show();
                return;
            }

            FoodOrderDTO orderDTO = new FoodOrderDTO(orderId, reservationId, orderType, status, total,
                    Date.valueOf(orderDate));
            boolean success;
            if (isEditMode) {
                success = FoodOrderModel.updateFoodOrder(orderDTO);
            } else {
                success = FoodOrderModel.saveFoodOrder(orderDTO);
            }

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, isEditMode ? "✅ Food order updated successfully" :
                        "✅ Food order saved successfully").show();
                resetPage();
                if (foodOrderTableController != null) {
                    foodOrderTableController.loadFoodOrderData();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, isEditMode ? "❌ Failed to update food order" :
                        "❌ Failed to save food order").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Database error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cmOrderType.setItems(FXCollections.observableArrayList("Dine-in", "Takeaway", "Room Service"));
            cmStatus.setItems(FXCollections.observableArrayList("Pending", "Completed", "Cancelled"));
            cmReservationId.setItems(FXCollections.observableArrayList(FoodOrderModel.getAllReservationIds()));

            lblOrderId.setText(FoodOrderModel.getNextOrderId());
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to initialize form: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void setFoodOrderData(FoodOrderDTO foodOrderDTO) {
        isEditMode = true;
        existingOrderId = foodOrderDTO.getOrderId();
        if (foodOrderDTO.getReservationId() != null) {
            lblOrderId.setText(foodOrderDTO.getOrderId());
            cmReservationId.getSelectionModel().select(foodOrderDTO.getReservationId());
            cmOrderType.getSelectionModel().select(foodOrderDTO.getOrderType());
            cmStatus.getSelectionModel().select(foodOrderDTO.getStatus());
            txtTotalAmount.setText(Double.toString(foodOrderDTO.getTotalAmount()));
            dpOrderDate.setValue(foodOrderDTO.getOrderDate().toLocalDate());

            cmReservationId.setDisable(false);
            cmOrderType.setDisable(false);
            cmStatus.setDisable(false);
            txtTotalAmount.setDisable(false);
            dpOrderDate.setDisable(false);
        }else {
            new Alert(Alert.AlertType.ERROR, "❌ No food Order data provided").show();
        }
    }

    public void setFoodOrderTableController(FoodOrderTableController foodOrderTableController) {
        this.foodOrderTableController = foodOrderTableController;
    }
}
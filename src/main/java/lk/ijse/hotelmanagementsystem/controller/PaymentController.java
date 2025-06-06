package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.PaymentDTO;
import lk.ijse.hotelmanagementsystem.model.PaymentModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML
    private Button btnClear;

    @FXML
    private ComboBox<String> cmReservationID;

    @FXML
    private TextField txtAmount;

    @FXML
    private ComboBox<String> cmPaymentMethod;

    @FXML
    private ComboBox<String> cmStatus;

    @FXML
    private Label lblMessage;

    @FXML
    private Button payButton;

    @FXML
    private Button btnCancel;

    @FXML
    private Label lblPaymentId;

    private final PaymentModel paymentModel = new PaymentModel();
    private boolean isEditMode = false;
    private PaymentTableController paymentTableController;
    private final String paymentIDValidation = "^PAY\\d{3}$";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmPaymentMethod.setItems(FXCollections.observableArrayList("Credit Card", "Cash", "Bank Transfer", "Online Payment"));
        cmStatus.setItems(FXCollections.observableArrayList("Pending", "Completed", "Failed", "Refunded"));
        try {
            String nextId = paymentModel.getNextPaymentId();
            if (nextId == null || !nextId.matches(paymentIDValidation)) {
                throw new SQLException("Invalid payment ID generated: " + nextId);
            }
            lblPaymentId.setText(nextId);
            cmReservationID.setItems(FXCollections.observableArrayList(paymentModel.loadReservationIds()));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to initialize payment data: " + e.getMessage()).show();
            lblPaymentId.setText("ERROR");
        }

        cmReservationID.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !isEditMode) {
                try {
                    loadAmountForReservation(newValue);
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, "Failed to load amount for reservation: " + e.getMessage()).show();
                    lblMessage.setText("Failed to load amount");
                    lblMessage.setStyle("-fx-text-fill: red;");
                }
            }
        });
    }

    @FXML
    public void processPayment(ActionEvent actionEvent) throws SQLException {
        String paymentId = lblPaymentId.getText();
        String reservationId = cmReservationID.getValue();
        String amountText = txtAmount.getText().trim();
        String paymentMethod = cmPaymentMethod.getValue();
        String status = cmStatus.getValue();
        String transactionId = "TX" + System.currentTimeMillis();
        LocalDateTime paymentDate = LocalDateTime.now();

        if (paymentId.isEmpty() || reservationId == null || amountText.isEmpty() || paymentMethod == null || status == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
            lblMessage.setText("Please fill all fields");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!paymentId.matches(paymentIDValidation)) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid Payment ID format (e.g., PAY001): " + paymentId).show();
            lblMessage.setText("Invalid Payment ID format");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                new Alert(Alert.AlertType.ERROR, "❌ Amount must be positive").show();
                lblMessage.setText("Amount must be positive");
                lblMessage.setStyle("-fx-text-fill: red;");
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid amount format").show();
            lblMessage.setText("Invalid amount format");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        PaymentDTO paymentDTO = new PaymentDTO(
                paymentId,
                reservationId,
                Timestamp.valueOf(paymentDate),
                amount,
                transactionId,
                paymentMethod,
                status
        );

        boolean success;
        if (isEditMode) {
            success = paymentModel.updatePayment(paymentDTO);
        } else {
            success = paymentModel.savePayment(paymentDTO);
        }

        if (success) {
            new Alert(Alert.AlertType.INFORMATION, isEditMode ? "✅ Payment updated successfully" : "✅ Payment saved successfully").show();
            lblMessage.setText(isEditMode ? "Payment updated successfully" : "Payment saved successfully");
            lblMessage.setStyle("-fx-text-fill: green;");
            if (paymentTableController != null) {
                paymentTableController.refreshTable();
            }
            Stage stage = (Stage) payButton.getScene().getWindow();
            stage.close();
        } else {
            new Alert(Alert.AlertType.ERROR, "❌ Operation failed").show();
            lblMessage.setText("Operation failed");
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void btnClearOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to clear the form?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            resetForm();
        }
    }

    @FXML
    public void btnCancelOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel and return to the payment table?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PaymentTable.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Hotel Management System - Payment Table");
                stage.centerOnScreen();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error navigating to payment table: " + e.getMessage()).show();
                lblMessage.setText("Error navigating to payment table: " + e.getMessage());
                lblMessage.setStyle("-fx-text-fill: red;");
            }
        }
    }

    private void resetForm() {
        try {
            String nextId = paymentModel.getNextPaymentId();
            if (nextId == null || !nextId.matches(paymentIDValidation)) {
                throw new SQLException("Invalid payment ID generated: " + nextId);
            }
            lblPaymentId.setText(nextId);
            cmReservationID.getSelectionModel().clearSelection();
            txtAmount.clear();
            cmPaymentMethod.getSelectionModel().clearSelection();
            cmStatus.getSelectionModel().clearSelection();
            lblMessage.setText("");
            lblMessage.setStyle("");
            payButton.setDisable(false);
            btnCancel.setDisable(false);
            isEditMode = false;
            lblPaymentId.setDisable(false);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to reset form: " + e.getMessage()).show();
            lblPaymentId.setText("ERROR");
            lblMessage.setText("Failed to reset form: " + e.getMessage());
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }

    private void loadAmountForReservation(String reservationId) throws SQLException {
        Double amount = paymentModel.getReservationTotalCost(reservationId);
        if (amount != null) {
            txtAmount.setText(String.format("%.2f", amount));
        } else {
            txtAmount.clear();
            lblMessage.setText("No total cost found for reservation: " + reservationId);
            lblMessage.setStyle("-fx-text-fill: orange;");
        }
    }

    public void setPaymentData(PaymentDTO paymentDTO) {
        isEditMode = true;
        if (paymentDTO != null) {
            String paymentId = paymentDTO.getPaymentId();
            if (paymentId == null || !paymentId.matches(paymentIDValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid payment ID in edit mode: " + paymentId).show();
                return;
            }
            lblPaymentId.setText(paymentId);
            cmReservationID.setValue(paymentDTO.getReservationId());
            txtAmount.setText(String.format("%.2f", paymentDTO.getAmount()));
            cmPaymentMethod.setValue(paymentDTO.getPaymentMethod());
            cmStatus.setValue(paymentDTO.getStatus());
            lblPaymentId.setDisable(true);
            lblMessage.setText("Editing payment: " + paymentId);
            lblMessage.setStyle("-fx-text-fill: blue;");
        } else {
            new Alert(Alert.AlertType.ERROR, "❌ No payment data provided").show();
            lblMessage.setText("No payment data provided");
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }

    public void setPaymentTableController(PaymentTableController paymentTableController) {
        this.paymentTableController = paymentTableController;
    }
}

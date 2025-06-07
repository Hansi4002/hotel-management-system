package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.PaymentDTO;
import lk.ijse.hotelmanagementsystem.model.PaymentModel;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentController implements Initializable {

    // Constants
    private static final String PAYMENT_ID_REGEX = "^PAY\\d{3}$";
    private static final String DEFAULT_TX_PREFIX = "TX";
    private static final double MAX_AMOUNT = 1_000_000.00;
    private static final Logger LOGGER = Logger.getLogger(PaymentController.class.getName());
    public TextField txtPaymentId;
    public DatePicker dpPaymentDate;
    public TextField txtTransactionId;

    // FXML Components
    @FXML private Button btnClear;
    @FXML private ComboBox<String> cmReservationID;
    @FXML private TextField txtAmount;
    @FXML private ComboBox<String> cmPaymentMethod;
    @FXML private ComboBox<String> cmStatus;
    @FXML private Label lblMessage;
    @FXML private Button payButton;
    @FXML private Button btnCancel;
    @FXML private Label lblPaymentId;
    @FXML private Label lblReservationDetails;

    // Dependencies
    private final PaymentModel paymentModel = new PaymentModel();
    private PaymentTableController paymentTableController;

    // State
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeComponents();
        setupEventHandlers();
        loadInitialData();
    }

    private void initializeComponents() {
        setupComboBoxes();
        lblMessage.setText("");
    }

    private void setupEventHandlers() {
        setupReservationSelectionListener();
    }

    private void loadInitialData() {
        generateNewPaymentId();
        loadReservationIds();
    }

    private void setupComboBoxes() {
        cmPaymentMethod.setItems(FXCollections.observableArrayList(
                "Credit Card", "Cash", "Bank Transfer", "Online Payment"));
        cmStatus.setItems(FXCollections.observableArrayList(
                "Pending", "Completed", "Failed", "Refunded"));
    }

    private void generateNewPaymentId() {
        try {
            String nextId = paymentModel.getNextPaymentId();
            if (!isValidPaymentId(nextId)) {
                throw new SQLException("Invalid payment ID format generated: " + nextId);
            }
            lblPaymentId.setText(nextId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate payment ID", e);
            showErrorAlert("System Error", "Failed to generate payment ID. Please try again.");
            lblPaymentId.setText("ERROR");
        }
    }

    private boolean isValidPaymentId(String id) {
        return id != null && id.matches(PAYMENT_ID_REGEX);
    }

    private void loadReservationIds() {
        try {
            List<String> reservationIds = paymentModel.loadReservationIds();
            cmReservationID.setItems(FXCollections.observableArrayList(reservationIds));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load reservation IDs", e);
            showErrorAlert("Database Error", "Failed to load reservation data");
        }
    }

    private void setupReservationSelectionListener() {
        cmReservationID.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && !isEditMode) {
                        loadReservationDetails(newValue);
                    }
                });
    }

    private void loadReservationDetails(String reservationId) {
        try {
            Double amount = paymentModel.getReservationTotalCost(reservationId);
            String reservationDetails = paymentModel.getReservationDetails(reservationId);

            if (amount != null) {
                txtAmount.setText(String.format("%.2f", amount));
                lblReservationDetails.setText(reservationDetails != null ?
                        reservationDetails : "No details available");
                clearMessage();
            } else {
                txtAmount.clear();
                showWarningAlert("No Amount Found", "No amount found for selected reservation");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load reservation details", e);
            showErrorAlert("Database Error", "Failed to load reservation details");
        }
    }

    @FXML
    private void processPayment(ActionEvent actionEvent) {
        if (!validateForm()) return;

        try {
            PaymentDTO paymentDTO = createPaymentDTO();
            boolean success = processPaymentDTO(paymentDTO);
            handlePaymentResult(success);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Payment processing failed", e);
            showErrorAlert("Payment Failed", "Failed to process payment. Please try again.");
        }
    }

    private boolean processPaymentDTO(PaymentDTO paymentDTO) throws SQLException {
        return isEditMode ?
                paymentModel.updatePayment(paymentDTO) :
                paymentModel.savePayment(paymentDTO);
    }

    private boolean validateForm() {
        if (!validateReservationSelection()) return false;
        if (!validateAmount()) return false;
        if (!validatePaymentMethod()) return false;
        return validatePaymentStatus();
    }

    private boolean validateReservationSelection() {
        if (cmReservationID.getValue() == null) {
            showWarningAlert("Validation Error", "Please select a reservation");
            return false;
        }
        return true;
    }

    private boolean validatePaymentMethod() {
        if (cmPaymentMethod.getValue() == null) {
            showWarningAlert("Validation Error", "Please select a payment method");
            return false;
        }
        return true;
    }

    private boolean validatePaymentStatus() {
        if (cmStatus.getValue() == null) {
            showWarningAlert("Validation Error", "Please select a payment status");
            return false;
        }
        return true;
    }

    private boolean validateAmount() {
        try {
            double amount = parseAmount();
            if (amount <= 0) {
                showErrorAlert("Invalid Amount", "Amount must be greater than zero");
                return false;
            }
            if (amount > MAX_AMOUNT) {
                showErrorAlert("Invalid Amount", "Amount exceeds maximum allowed value");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Amount", "Please enter a valid numeric amount");
            return false;
        }
    }

    private double parseAmount() throws NumberFormatException {
        return Double.parseDouble(txtAmount.getText().trim());
    }

    private PaymentDTO createPaymentDTO() {
        return new PaymentDTO(
                lblPaymentId.getText(),
                cmReservationID.getValue(),
                Timestamp.valueOf(LocalDateTime.now()),
                parseAmount(),
                generateTransactionId(),
                cmPaymentMethod.getValue(),
                cmStatus.getValue()
        );
    }

    private String generateTransactionId() {
        return String.format("%s-%d-%04d",
                DEFAULT_TX_PREFIX,
                System.currentTimeMillis(),
                ThreadLocalRandom.current().nextInt(1000));
    }

    private void handlePaymentResult(boolean success) {
        if (success) {
            notifySuccess();
            refreshPaymentTable();
            closeWindow();
        } else {
            notifyFailure();
        }
    }

    private void notifySuccess() {
        String title = isEditMode ? "Payment Updated" : "Payment Processed";
        String message = isEditMode ? "Payment updated successfully" : "Payment processed successfully";
        showSuccessAlert(title, message);
    }

    private void notifyFailure() {
        String message = isEditMode ? "Failed to update payment" : "Failed to process payment";
        showErrorAlert("Operation Failed", message);
    }

    private void refreshPaymentTable() {
        if (paymentTableController != null) {
            paymentTableController.refreshTable();
        }
    }

    @FXML
    private void btnClearOnAction(ActionEvent actionEvent) {
        showConfirmationDialog("Clear Form",
                "Are you sure you want to clear the form?",
                this::resetForm);
    }

    @FXML
    private void btnCancelOnAction(ActionEvent actionEvent) {
        showConfirmationDialog("Cancel Payment",
                "Are you sure you want to cancel?",
                this::closeWindow);
    }

    private void resetForm() {
        generateNewPaymentId();
        clearFormFields();
        resetEditMode();
        clearMessage();
    }

    private void clearFormFields() {
        cmReservationID.getSelectionModel().clearSelection();
        txtAmount.clear();
        cmPaymentMethod.getSelectionModel().clearSelection();
        cmStatus.getSelectionModel().clearSelection();
        lblReservationDetails.setText("");
    }

    private void resetEditMode() {
        isEditMode = false;
        lblPaymentId.setDisable(false);
    }

    private void clearMessage() {
        lblMessage.setText("");
    }

    private void closeWindow() {
        Stage stage = (Stage) payButton.getScene().getWindow();
        stage.close();
    }

    public void setPaymentData(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            showErrorAlert("Invalid Data", "No payment data provided");
            return;
        }

        initializeEditMode(paymentDTO);
    }

    private void initializeEditMode(PaymentDTO paymentDTO) {
        isEditMode = true;
        setFormFields(paymentDTO);
        lblPaymentId.setDisable(true);
        loadReservationDetails(paymentDTO.getReservationId());
    }

    private void setFormFields(PaymentDTO paymentDTO) {
        lblPaymentId.setText(paymentDTO.getPaymentId());
        cmReservationID.setValue(paymentDTO.getReservationId());
        txtAmount.setText(String.format("%.2f", paymentDTO.getAmount()));
        cmPaymentMethod.setValue(paymentDTO.getPaymentMethod());
        cmStatus.setValue(paymentDTO.getStatus());
    }

    public void setPaymentTableController(PaymentTableController paymentTableController) {
        this.paymentTableController = paymentTableController;
    }

    // Alert helper methods
    private void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
        setMessage(message, "red");
    }

    private void showWarningAlert(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
        setMessage(message, "orange");
    }

    private void showSuccessAlert(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
        setMessage(message, "green");
    }

    private void setMessage(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + color + ";");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmationDialog(String title, String content, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            onConfirm.run();
        }
    }

    public void initializeWithData(PaymentDTO paymentData) {
    }

    public void setParentController(PaymentTableController paymentTableController) {

    }
}
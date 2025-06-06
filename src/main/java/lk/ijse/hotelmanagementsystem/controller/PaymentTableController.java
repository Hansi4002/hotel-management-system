package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.PaymentDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.PaymentTM;
import lk.ijse.hotelmanagementsystem.model.PaymentModel;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

public class PaymentTableController {

    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private Button btnCancel;
    @FXML private Button btnEdit;
    @FXML private Button btnAddPayment;
    @FXML private Button btnGenerateReport;
    @FXML private Button btnSendEmail;
    @FXML private TableView<PaymentTM> tblPayments;
    @FXML private TableColumn<PaymentTM, String> colPaymentId;
    @FXML private TableColumn<PaymentTM, String> colReservationId;
    @FXML private TableColumn<PaymentTM, Timestamp> colPaymentDate;
    @FXML private TableColumn<PaymentTM, Double> colAmount;
    @FXML private TableColumn<PaymentTM, String> colTransactionID;
    @FXML private TableColumn<PaymentTM, String> colPaymentMethod;
    @FXML private TableColumn<PaymentTM, String> colStatus;

    private final PaymentModel paymentModel = new PaymentModel();
    private final ObservableList<PaymentTM> paymentList = FXCollections.observableArrayList();

    private final String mailUsername = "paboda4002@gmail.com";
    private final String mailPassword = "buub wgvh fije rqfu";

    @FXML
    public void initialize() {
        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colReservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        colPaymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colTransactionID.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colPaymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblPayments.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        try {
            loadPaymentData();
        } catch (SQLException e) {
            showError("Failed to load payment data", e);
        }
    }

    private void loadPaymentData() throws SQLException {
        List<PaymentDTO> paymentDTOList = paymentModel.getAllPayments();
        paymentList.setAll(mapToPaymentTMList(paymentDTOList));
        tblPayments.setItems(paymentList);
    }

    private List<PaymentTM> mapToPaymentTMList(List<PaymentDTO> dtos) {
        return dtos.stream()
                .map(this::mapToPaymentTM)
                .toList();
    }

    private PaymentTM mapToPaymentTM(PaymentDTO dto) {
        return new PaymentTM(
                dto.getPaymentId(),
                dto.getReservationId(),
                dto.getPaymentDate(),
                dto.getAmount(),
                dto.getTransactionId(),
                dto.getPaymentMethod(),
                dto.getStatus()
        );
    }

    @FXML
    public void btnSearchOnAction(ActionEvent event) {
        String searchTerm = txtSearch.getText().trim();
        try {
            List<PaymentDTO> results = paymentModel.searchPayments(searchTerm);
            paymentList.setAll(mapToPaymentTMList(results));
        } catch (SQLException e) {
            showError("Failed to search payments", e);
        }
    }

    @FXML
    public void btnCancelOnAction(ActionEvent event) {
        PaymentTM selected = tblPayments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a payment to cancel.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel this payment?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean deleted = paymentModel.deletePayment((String) selected.getPaymentId());
                    if (deleted) {
                        paymentList.remove(selected);
                        showInfo("Payment canceled successfully.");
                    } else {
                        showError("Failed to cancel payment.");
                    }
                } catch (SQLException e) {
                    showError("Error canceling payment", e);
                }
            }
        });
    }

    @FXML
    public void btnAddPaymentOnAction(ActionEvent event) {
        openPaymentWindow(null, "Add Payment");
    }

    @FXML
    public void btnEditOnAction(ActionEvent event) {
        PaymentTM selected = tblPayments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a payment to edit.");
            return;
        }

        PaymentDTO dto = new PaymentDTO(
                (String) selected.getPaymentId(),
                selected.getReservationId(),
                selected.getPaymentDate(),
                selected.getAmount(),
                (String) selected.getTransactionId(),
                selected.getPaymentMethod(),
                selected.getStatus()
        );

        openPaymentWindow(dto, "Edit Payment");
    }

    private void openPaymentWindow(PaymentDTO paymentData, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PaymentView.fxml"));
            Parent root = loader.load();
            PaymentController controller = loader.getController();

            if (paymentData != null) {
                controller.setPaymentData(paymentData);
            }
            controller.setPaymentTableController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Hotel Management System - " + title);
            stage.show();

            stage.setOnHiding(event -> {
                try {
                    loadPaymentData();
                } catch (SQLException e) {
                    showError("Failed to refresh payment data", e);
                }
            });
        } catch (IOException e) {
            showError("Failed to open payment window", e);
        }
    }

    @FXML
    public void btnGenerateReportOnAction(ActionEvent event) {
        showInfo("Report generation not implemented yet.");
    }

    @FXML
    public void btnSendEmailOnAction(ActionEvent event) {
        PaymentTM selected = tblPayments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a payment to email.");
            return;
        }

        try {
            sendPaymentEmail(selected);
            showInfo("Email sent successfully.");
        } catch (MessagingException e) {
            showError("Failed to send email", e);
        }
    }

    private void sendPaymentEmail(PaymentTM payment) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "2525");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailUsername, mailPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailUsername));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("recipient@example.com"));
        message.setSubject("Payment Details");
        message.setText(String.format(
                "Payment ID: %s%nReservation ID: %s%nAmount: %.2f%nStatus: %s",
                payment.getPaymentId(), payment.getReservationId(), payment.getAmount(), payment.getStatus()));

        Transport.send(message);
    }

    public void refreshTable() throws SQLException {
        loadPaymentData();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    private void showError(String message, Exception e) {
        new Alert(Alert.AlertType.ERROR, message + "\n" + e.getMessage()).showAndWait();
    }

    private void showWarning(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }

    private void showInfo(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }
}
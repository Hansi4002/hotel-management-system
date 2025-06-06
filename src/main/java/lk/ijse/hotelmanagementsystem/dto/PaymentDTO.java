package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

public class PaymentDTO {
    private String paymentID;
    private String reservationID;
    private Timestamp paymentDate;
    private double amount;
    private String transactionID;
    private String paymentMethod;
    private String status;

    public PaymentDTO(String paymentId, String reservationId, Timestamp timestamp, double amount, String transactionId, String paymentMethod, String status) {
    }

    public String getPaymentId() {
        return paymentID;
    }

    public String getReservationId() {
        return reservationID;
    }

    public String getTransactionId() {
        return transactionID;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }
}

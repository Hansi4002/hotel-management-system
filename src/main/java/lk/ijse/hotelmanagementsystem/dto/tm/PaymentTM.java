package lk.ijse.hotelmanagementsystem.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentTM {
    private String paymentID;
    private String reservationID;
    private Timestamp paymentDate;
    private double amount;
    private String transactionID;
    private String paymentMethod;
    private String status;

    public String getReservationId() {
        return reservationID;
    }

    public Object getPaymentId() {
        return paymentID;
    }

    public Object getTransactionId() {
        return transactionID;
    }
}

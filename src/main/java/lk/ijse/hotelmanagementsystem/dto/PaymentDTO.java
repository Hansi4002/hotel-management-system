package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDTO {
    private String paymentID;
    private String reservationID;
    private String paymentDate;
    private double amount;
    private String transactionID;
    private String paymentMethod;
    private String status;
}

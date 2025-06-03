package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FoodOrderDTO {
    private String orderId;
    private String reservationId;
    private String orderType;
    private String status;
    private double totalAmount;
    private Date orderDate;

    public FoodOrderDTO(String orderId, String reservationId, String orderType, String status, Double totalAmount, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.reservationId = reservationId;
        this.orderType = orderType;
        this.status = status;
        this.totalAmount = totalAmount != null ? totalAmount : 0.0;
        this.orderDate = orderDate != null ? Date.valueOf(orderDate.toLocalDate()) : null;
    }

    public String getReservationID() {
        return reservationId;
    }

    public String getReservationId() {
        return reservationId;
    }
}
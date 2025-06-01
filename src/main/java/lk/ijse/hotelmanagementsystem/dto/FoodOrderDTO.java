package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

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
}

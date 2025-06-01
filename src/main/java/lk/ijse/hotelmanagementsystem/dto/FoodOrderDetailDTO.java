package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FoodOrderDetailDTO {
    private String menuId;
    private String orderId;
    private double price;
    private int quantity;
}

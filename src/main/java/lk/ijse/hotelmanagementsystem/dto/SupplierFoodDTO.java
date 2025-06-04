package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplierFoodDTO {
    private String menuId;
    private String supplierId;
    private double cost;
    private int quantity;
    private Date supplyDate;

    public SupplierFoodDTO(String menuId, String supplierId, double costDouble, int quantityInt) {
    }
}

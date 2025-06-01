package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FoodDTO {
    private String menuId;
    private boolean available;
    private String category;
    private Double price;
    private String itemName;
    private String description;
}

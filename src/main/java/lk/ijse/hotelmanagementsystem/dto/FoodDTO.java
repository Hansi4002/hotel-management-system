package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodDTO {
    private String menuId;
    private String available;
    private String category;
    private double price;
    private String itemName;
    private String description;

    public FoodDTO(String menuId, String itemName, String description, String category, String available, double price) {
        this.menuId = menuId;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.available = available;
        this.price = price;
    }
    public Object getAvailable() {return available;}

    public String isAvailable() {return available;}
}
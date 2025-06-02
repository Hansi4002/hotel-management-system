package lk.ijse.hotelmanagementsystem.dto.tm;

public class FoodTM {
    private String menuId;
    private boolean available;
    private String category;
    private Double price;
    private String itemName;
    private String description;

    public FoodTM(String menuId, boolean available, String category, Double price, String itemName, String description) {
        this.menuId = menuId;
        this.available = available;
        this.category = category;
        this.price = price;
        this.itemName = itemName;
        this.description = description;
    }

    public String getMenuId() {
        return menuId;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }
}

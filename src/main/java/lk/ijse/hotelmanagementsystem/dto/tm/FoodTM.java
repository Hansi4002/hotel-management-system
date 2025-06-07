package lk.ijse.hotelmanagementsystem.dto.tm;

public class FoodTM {
    private String menuId;
    private String available;
    private String category;
    private double price;
    private String itemName;
    private String description;

    public FoodTM(String menuId, String itemName, String description, String category, String availabl, double price) {
        this.menuId = menuId;
        this.available = availabl;
        this.category = category;
        this.price = price;
        this.itemName = itemName;
        this.description = description;
    }

    public String getMenuId() {
        return menuId;
    }

    public String  isAvailable() {return available;}

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public void setAvailable(String available) {this.available = available;}

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailable() {return available;}
}
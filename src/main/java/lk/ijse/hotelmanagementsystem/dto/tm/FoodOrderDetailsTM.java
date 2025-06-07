package lk.ijse.hotelmanagementsystem.dto.tm;

import lombok.Setter;

@Setter
public class FoodOrderDetailsTM {
    private String menuId;
    private String orderId;
    private double price;
    private int quantity;

    public FoodOrderDetailsTM(String menuId, String orderId, Object itemPrice, int quantity) {
        this.quantity = quantity;
        this.price = (double) itemPrice;
        this.orderId = orderId;
        this.menuId = menuId;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getItemPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
}

package lk.ijse.hotelmanagementsystem.dto.tm;

import java.sql.Date;

public class SupplierFoodTM {
    private String menuId;
    private String supplierId;
    private double cost;
    private int quantity;
    private Date supplyDate;

    public SupplierFoodTM(String menuId, String supplierId, double cost, int quantity, Date supplyDate) {
        this.menuId = menuId;
        this.supplierId = supplierId;
        this.cost = cost;
        this.quantity = quantity;
        this.supplyDate = supplyDate;
    }

    public String getMenuId() {
        return menuId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public double getCost() {
        return cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getSupplyDate() {
        return supplyDate;
    }

    public Double getTotalCost() {
        return cost * quantity;
    }
}

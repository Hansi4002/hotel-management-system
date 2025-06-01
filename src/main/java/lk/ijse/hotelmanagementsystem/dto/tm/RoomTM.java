package lk.ijse.hotelmanagementsystem.dto.tm;

public class RoomTM {
    private String roomId;
    private String roomType;
    private double price;
    private String status;
    private String floorNumber;
    private int capacity;
    private String description;
    private String roomNumber;

    public RoomTM(String roomId, String roomType, double price, String status, String floorNumber, int capacity, String description, String roomNumber) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.price = price;
        this.status = status;
        this.floorNumber = floorNumber;
        this.capacity = capacity;
        this.description = description;
        this.roomNumber = roomNumber;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }

    public String getRoomNumber() {
        return roomNumber;
    }
}

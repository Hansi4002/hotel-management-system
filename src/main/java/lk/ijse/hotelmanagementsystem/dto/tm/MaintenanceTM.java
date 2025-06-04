package lk.ijse.hotelmanagementsystem.dto.tm;

public class MaintenanceTM {
    private String maintenanceId;
    private String roomId;
    private String staffId;
    private String description;
    private String status;

    public MaintenanceTM(String maintenanceId, String roomId, String staffId, String description, String status) {
        this.maintenanceId = maintenanceId;
        this.roomId = roomId;
        this.staffId = staffId;
        this.description = description;
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getMaintenanceId() {
        return maintenanceId;
    }
}

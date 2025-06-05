package lk.ijse.hotelmanagementsystem.dto.tm;

import java.sql.Date;

public class StaffTM {
    private String staffId;
    private String userId;
    private String name;
    private String position;
    private String contact;
    private Date hireDate;

    public StaffTM(String staffId, String userId, String name, String position, String contact, Date hireDate) {
        this.staffId = staffId;
        this.userId = userId;
        this.name = name;
        this.position = position;
        this.contact = contact;
        this.hireDate = hireDate;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getContact() {
        return contact;
    }

    public Date getHireDate() {
        return hireDate;
    }
}

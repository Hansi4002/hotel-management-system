package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffDTO {
    private String staffId;
    private String userId;
    private String name;
    private String position;
    private String contact;
    private Date hireDate;

    public StaffDTO(String staffId, String name, Object role, String contact, Object email, Object address) {
    }
}

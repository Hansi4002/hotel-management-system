package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GuestDTO {
    private String guestId;
    private String name;
    private Date dob;
    private String address;
    private String contact;
    private String email;
    private Date registrationDate;
    private String loyaltyStatus;
}

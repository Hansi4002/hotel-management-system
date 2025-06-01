package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private String userId;
    private String firstName;
    private String lastName;
    private String role;
    private String passwordHash;
    private String username;
    private LocalDateTime lastLogin;
    private boolean isActive;
    private String email;

}
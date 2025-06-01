package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaintenanceDTO {
    private String maintananceId;
    private String roomId;
    private String staffId;
    private String description;
    private String status;
}

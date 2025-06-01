package lk.ijse.hotelmanagementsystem.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoomDTO {
    private String roomId;
    private String roomType;
    private double price;
    private String status;
    private String floorNumber;
    private int capacity;
    private String description;
    private String roomNumber;
}

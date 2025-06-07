package lk.ijse.hotelmanagementsystem.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservationTM {
    private String reservationId;
    private String guestId;
    private String roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private Timestamp bookingTime;
    private int numberOfGuests;
    private String status;
    private double totalCost;

    public ReservationTM(String reservationId, String guestId, String roomId, Date checkInDate, Date checkOutDate, int numberOfGuests, String status, double totalCost) {
    }
}
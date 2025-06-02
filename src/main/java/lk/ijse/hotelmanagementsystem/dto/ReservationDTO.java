package lk.ijse.hotelmanagementsystem.dto;

import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
public class ReservationDTO {
    private String reservationID;
    private String guestID;
    private String roomID;
    private Date checkInDate;
    private Date checkOutDate;
    private Timestamp bookingTime;
    private int numberOfGuests;
    private String status;
    private double totalCost;

    public ReservationDTO(String reservationID, String guestID, String roomID, Date checkInDate, Date checkOutDate, Timestamp bookingTime, int numberOfGuests, String status, double totalCost) {
        this.reservationID = reservationID;
        this.guestID = guestID;
        this.roomID = roomID;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingTime = bookingTime;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.totalCost = totalCost;
    }

    public String getReservationID() {
        return reservationID;
    }

    public String getGuestID() {
        return guestID;
    }

    public String getRoomID() {
        return roomID;
    }

    public Date getCheckInDate() {return checkInDate;}

    public Date getCheckOutDate() {return checkOutDate;}

    public Timestamp getBookingTime() {return bookingTime;}

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public String getStatus() {
        return status;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getRoomId() {return roomID;}

    public String getGuestId() {return guestID;}

    public String getReservationId() {return reservationID;}
}

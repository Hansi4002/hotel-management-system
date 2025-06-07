package lk.ijse.hotelmanagementsystem.dto;

import java.sql.Date;
import java.sql.Timestamp;

public class ReservationDTO {
    private String reservationId;
    private String guestId;
    private String roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private Timestamp bookingTime;
    private int numberOfGuests;
    private String status;
    private double totalCost;

    public ReservationDTO(String reservationId, String guestId, String roomId, Date checkInDate, Date checkOutDate,
                          Timestamp bookingTime, int numberOfGuests, String status, double totalCost) {
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingTime = bookingTime;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.totalCost = totalCost;
    }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }
    public Timestamp getBookingTime() { return bookingTime; }
    public void setBookingTime(Timestamp bookingTime) { this.bookingTime = bookingTime; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
}
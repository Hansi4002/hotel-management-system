package lk.ijse.hotelmanagementsystem.dto.tm;

import java.sql.Date;

public class GuestTM {
    private String guestId;
    private String name;
    private Date dob;
    private String address;
    private String contact;
    private String email;
    private Date registrationDate;
    private String loyaltyStatus;

    public GuestTM(String guestId, String name, Date dob, String address, String contact, String email, Date registrationDate, String loyaltyStatus) {
        this.guestId = guestId;
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.contact = contact;
        this.email = email;
        this.registrationDate = registrationDate;
        this.loyaltyStatus = loyaltyStatus;
    }

    public String getGuestId() {
        return guestId;
    }

    public String getName() {
        return name;
    }

    public Date getDob() {
        return dob;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public String getLoyaltyStatus() {
        return loyaltyStatus;
    }
}

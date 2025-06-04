package lk.ijse.hotelmanagementsystem.dto.tm;

public class SupplierTM {
    private String supplierId;
    private String name;
    private String contact;
    private String email;
    private String address;

    public SupplierTM(String supplierId, String name, String contact, String email, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}

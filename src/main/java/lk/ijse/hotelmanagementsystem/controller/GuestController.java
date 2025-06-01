//package lk.ijse.hotelmanagementsystem.controller;
//
//import javafx.collections.FXCollections;
//import javafx.event.ActionEvent;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import lk.ijse.hotelmanagementsystem.dto.GuestDTO;
//import lk.ijse.hotelmanagementsystem.dto.tm.GuestTM;
////import lk.ijse.hotelmanagementsystem.model.GuestModel;
//
//import java.sql.Date;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Optional;
//
//public class GuestController {
//    public Button btnLogout;
//    public Button btnViewGuests;
//    public Button btnAddGuestNavigation;
//    public Button btnBackToMenu;
//    public Button btnEdit;
//    public Label lblGuestId;
//    public TextField txtName;
//    public DatePicker dpDOB;
//    public TextField txtEmail;
//    public TextField txtContact;
//    public TextField txtAddress;
//    public DatePicker dpRegistrationDate;
//    public ComboBox cmLoyaltyStatus;
//    public Button btnSave;
//    public Button btnDelete;
//    public Button btnAddGuest;
//
//    public TableView<GuestTM> tblGuests;
//    public TableColumn<GuestTM,String> colGuestId;
//    public TableColumn<GuestTM,String> colName;
//    public TableColumn<GuestTM,String> colAddress;
//    public TableColumn<GuestTM, Date> colDOB;
//    public TableColumn<GuestTM,String> colContact;
//    public TableColumn<GuestTM,String> colEmail;
//    public TableColumn<GuestTM,Date> colRegistrationDate;
//    public TableColumn<GuestTM,String> colLoyaltyStatus;
//
//    private final GuestModel guestModel = new GuestModel();
//
//    private final String namePattern = "^[A-Za-z ]+$";
//    private final String emailPattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
//    private final String contactPattern = "^\\d{10}$";
//    private final String addressPattern = "^[A-Za-z0-9 ,.-]+$";
//
//    public void initialize() {
//        colGuestId.setCellValueFactory(new PropertyValueFactory<>("guestId"));
//        colName.setCellValueFactory(new PropertyValueFactory<>("guestName"));
//        colDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));
//        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
//        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
//        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
//        colRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
//        colLoyaltyStatus.setCellValueFactory(new PropertyValueFactory<>("loyaltyStatus"));
//
//        cmLoyaltyStatus.getItems().addAll("Silver", "Gold","Platinum");
//        try {
//            resetPage();
//        }catch (Exception e){
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR,"Something went wrong when trying to reset the page").show();
//        }
//    }
//
//    private void resetPage() {
//        try {
//            loadTableDate();
//            loadNextId();
//            btnSave.setDisable(false);
//            btnDelete.setDisable(false);
//            btnEdit.setDisable(false);
//
//            txtName.setText("");
//            dpDOB.setValue(null);
//            txtAddress.setText("");
//            txtContact.setText("");
//            txtEmail.setText("");
//            dpRegistrationDate.setValue(null);
//            cmLoyaltyStatus.setValue(null);
//        }catch(Exception e){
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR,"Something went wrong while resetting the page").show();
//        }
//    }
//
//    private void loadNextId() throws SQLException, ClassNotFoundException {
//        String nextId = GuestModel.getNextGuestId();
//        lblGuestId.setText(nextId);
//    }
//
//    private void loadTableDate() throws SQLException, ClassNotFoundException {
//        List<GuestDTO> guests = guestModel.getAllGuest();
//            List<GuestTM> guestTMS = guests.stream().map(guest -> new GuestTM(
//                guest.getGuestId(),
//                guest.getName(),
//                guest.getDob(),
//                guest.getAddress(),
//                guest.getContact(),
//                guest.getEmail(),
//                guest.getRegistrationDate(),
//                guest.getLoyaltyStatus()
//        )).toList();
//        tblGuests.setItems(FXCollections.observableArrayList(guestTMS));
//    }
//
//    public void btnLogoutOnAction(ActionEvent actionEvent) {
//    }
//
//    public void btnEditOnAction(ActionEvent actionEvent) {
//        String guestId = lblGuestId.getText();
//        String name = txtName.getText();
//        Date dob = Date.valueOf(dpDOB.getValue());
//        String address = txtAddress.getText();
//        String contact = txtContact.getText();
//        String email = txtEmail.getText();
//        Date regDate = Date.valueOf(dpRegistrationDate.getValue());
//        String loyaltyStatus = (String) cmLoyaltyStatus.getValue();
//
//        GuestDTO guestDTO = new GuestDTO(guestId, name, dob,address,contact,email,regDate,loyaltyStatus);
//
//        try {
//            boolean isUpdated = guestModel.updateGuest(guestDTO);
//            if(isUpdated){
//                resetPage();
//                new Alert(Alert.AlertType.INFORMATION,"Guest updated successfully").show();
//            }else {
//                new Alert(Alert.AlertType.ERROR,"Guest update failed").show();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR,"Something went wrong when trying to update guest").show();
//        }
//    }
//
//    public void btnSaveOnAction(ActionEvent actionEvent) {
//        String guestId = lblGuestId.getText();
//        String name = txtName.getText();
//        Date dob = Date.valueOf(dpDOB.getValue());
//        String address = txtAddress.getText();
//        String contact = txtContact.getText();
//        String email = txtEmail.getText();
//        Date regDate = Date.valueOf(dpRegistrationDate.getValue());
//        String loyaltyStatus = (String) cmLoyaltyStatus.getValue();
//
//        boolean isValidname = name.matches(namePattern);
//        boolean isValidemail = email.matches(emailPattern);
//        boolean isValidaddress = address.matches(addressPattern);
//        boolean isValidcontact = contact.matches(contactPattern);
//
//        txtName.setStyle(txtName.getStyle()+";-fx-border-color: #7367F0;");
//        txtAddress.setStyle(txtAddress.getStyle()+";-fx-border-color: #7367F0;");
//        txtEmail.setStyle(txtEmail.getStyle()+";-fx-border-color: #7367F0");
//        txtContact.setStyle(txtContact.getStyle()+";-fx-border-color: #7367F0");
//
//        if(!isValidname)txtName.setStyle(txtName.getStyle()+";-fx-border-color: red");
//        if (!isValidaddress)txtAddress.setStyle(txtAddress.getStyle()+";-fx-border-color: red");
//        if (!isValidemail)txtEmail.setStyle(txtEmail.getStyle()+";-fx-border-color: red");
//        if (!isValidcontact)txtContact.setStyle(txtContact.getStyle()+";-fx-border-color: red");
//
//        if (dpDOB.getValue() == null || dpRegistrationDate.getValue() == null) {
//            new Alert(Alert.AlertType.ERROR, "Please fill all date fields").show();
//            return;
//        }
//
//        GuestDTO guestDTO = new GuestDTO(guestId, name, dob, address, contact,email,regDate,loyaltyStatus);
//        if(isValidname && isValidaddress && isValidcontact && isValidemail){
//            try {
//                boolean isSaved = guestModel.saveGuest(guestDTO);
//                if(isSaved){
//                    resetPage();
//                    new Alert(Alert.AlertType.INFORMATION,"Guest saved successfully").show();
//                }else {
//                    new Alert(Alert.AlertType.ERROR,"Guest saved failed").show();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//                new Alert(Alert.AlertType.ERROR,"Something went wrong when trying to save guest").show();
//            }
//        }
//    }
//
//    public void btnDeleteOnAction(ActionEvent actionEvent) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to delete this guest?",ButtonType.YES,ButtonType.NO);
//        Optional<ButtonType> response = alert.showAndWait();
//        if (response.isPresent() && response.get() == ButtonType.YES){
//            String guestId = lblGuestId.getText();
//            try {
//                boolean isDeleted = guestModel.deleteGuest(guestId);
//                if(isDeleted){
//                    resetPage();
//                    new Alert(Alert.AlertType.INFORMATION,"Guest deleted successfully").show();
//                }else{
//                    new Alert(Alert.AlertType.ERROR,"Guest delete failed").show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                new Alert(Alert.AlertType.ERROR,"Something went wrong when trying to delete guest").show();
//            }
//        }
//    }
//
//    public void btnAddNewGuestOnAction(ActionEvent actionEvent) {
//        resetPage();
//        try {
//            loadNextId();
//        }catch (Exception e){
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR,"Something went wrong when trying to add guest").show();
//        }
//        btnSave.setDisable(false);
//        btnDelete.setDisable(true);
//        btnEdit.setDisable(true);
//    }
//}

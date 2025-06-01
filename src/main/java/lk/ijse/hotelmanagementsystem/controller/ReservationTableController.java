//package lk.ijse.hotelmanagementsystem.controller;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.scene.control.Button;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import lk.ijse.hotelmanagementsystem.dto.tm.ReservationTM;
//import lk.ijse.hotelmanagementsystem.model.ReservationModel;
//
//import java.util.Date;
//import java.util.List;
//
//public class ReservationTableController {
//    public Button btnLogout;
//    public Button btnAddReservation;
//    public TableColumn<ReservationTM,String> colReservationId;
//    public TableColumn<ReservationTM,String> colGuestId;
//    public TableColumn<ReservationTM,String> colRoomId;
//    public TableColumn<ReservationTM, Date> colCheckInDate;
//    public TableColumn<ReservationTM,Date> colCheckOutDate;
//    public TableColumn<ReservationTM,Date> colBookingTime;
//    public TableColumn<ReservationTM,String> colNumberOfGuests;
//    public TableColumn<ReservationTM,Integer> colStatus;
//    public TableColumn<ReservationTM,Double> colTotalCost;
//    public TableView<ReservationTM> tblReservation;
//
//    public void initialize() {
//        colReservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
//        colGuestId.setCellValueFactory(new PropertyValueFactory<>("guestId"));
//        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
//        colCheckInDate.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
//        colCheckOutDate.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
//        colBookingTime.setCellValueFactory(new PropertyValueFactory<>("bookingTime"));
//        colNumberOfGuests.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
//        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
//        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
//
//        loadReservations();
//    }
//
//    private void loadReservations() {
//        List<ReservationTM> reservations = ReservationModel.getAllReservations();
//        ObservableList<ReservationTM> reservationObservableList = FXCollections.observableArrayList(reservations);
//        tblReservation.setItems(reservationObservableList);
//    }
//
//    public void btnLogoutOnAction(ActionEvent actionEvent) {
//    }
//
//    public void btnAddNewReservationOnAction(ActionEvent actionEvent) {
//    }
//}

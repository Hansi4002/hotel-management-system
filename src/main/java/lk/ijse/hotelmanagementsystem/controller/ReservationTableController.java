package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.ReservationDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.ReservationTM;
import lk.ijse.hotelmanagementsystem.model.ReservationModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationTableController implements Initializable {
    public Button btnLogout;
    public TableColumn<ReservationTM, String> colReservationId;
    public TableColumn<ReservationTM, String> colGuestId;
    public TableColumn<ReservationTM, String> colRoomId;
    public TableColumn<ReservationTM, Date> colCheckInDate;
    public TableColumn<ReservationTM, Date> colCheckOutDate;
    public TableColumn<ReservationTM, Timestamp> colBookingTime;
    public TableColumn<ReservationTM, String> colNumberOfGuests;
    public TableColumn<ReservationTM, Integer> colStatus;
    public TableColumn<ReservationTM, Double> colTotalCost;
    public TableView<ReservationTM> tblReservation;
    public Button btnEdit;
    public Button btnCancel;
    public Button btnAddReservation;
    public TextField txtSearch;
    public Button btnSearch;

    private final ReservationModel reservationModel = new ReservationModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colReservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        colGuestId.setCellValueFactory(new PropertyValueFactory<>("guestId"));
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colCheckInDate.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOutDate.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        colBookingTime.setCellValueFactory(new PropertyValueFactory<>("bookingTime"));
        colNumberOfGuests.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        tblReservation.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loadReservationData();
    }

    public void btnAddNewReservationOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReservationView.fxml"));
            Parent load = loader.load();
            ReservationController controller = loader.getController();
            controller.setReservationTableController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Reservation");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Add Reservation window").show();
        }
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        ReservationTM selected = tblReservation.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a reservation to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReservationView.fxml"));
            Parent load = loader.load();

            ReservationController controller = loader.getController();
            controller.setReservationData(new ReservationDTO(
                    selected.getReservationId(),
                    selected.getRoomId(),
                    selected.getGuestId(),
                    selected.getCheckInDate(),
                    selected.getCheckOutDate(),
                    selected.getBookingTime(),
                    selected.getNumberOfGuests(),
                    selected.getStatus(),
                    selected.getTotalCost()
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Reservation");
            stage.show();

            stage.setOnHiding(event -> loadReservationData());

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open edit reservation window").show();
        }
    }

    void loadReservationData() {
        try {
            List<ReservationDTO> reservationList = reservationModel.getAllReservations();
            List<ReservationTM> tmList = reservationList.stream().map(dto -> new ReservationTM(
                    dto.getReservationId(),
                    dto.getRoomId(),
                    dto.getGuestId(),
                    dto.getCheckInDate(),
                    dto.getCheckOutDate(),
                    dto.getBookingTime(),
                    dto.getNumberOfGuests(),
                    dto.getStatus(),
                    dto.getTotalCost()
            )).toList();

            ObservableList<ReservationTM> observableList = FXCollections.observableArrayList(tmList);
            tblReservation.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load reservation data").show();
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        ReservationTM selectedReservation = tblReservation.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a reservation to cancel").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel reservation " + selectedReservation.getReservationId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean isDeleted = reservationModel.deleteReservation(selectedReservation.getReservationId());
                    if (isDeleted) {
                        tblReservation.getItems().remove(selectedReservation);
                        new Alert(Alert.AlertType.INFORMATION, "Reservation canceled successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to cancel reservation").show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Error canceling reservation: " + e.getMessage()).show();
                }
            }
        });
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        String searchText = txtSearch.getText().trim();

        if (searchText.isEmpty()) {
            loadReservationData();
            return;
        }

        try {
            ReservationDTO dto = reservationModel.searchReservationById(searchText);
            if (dto != null) {
                ReservationTM tm = new ReservationTM(
                        dto.getReservationId(),
                        dto.getRoomId(),
                        dto.getGuestId(),
                        dto.getCheckInDate(),
                        dto.getCheckOutDate(),
                        dto.getBookingTime(),
                        dto.getNumberOfGuests(),
                        dto.getStatus(),
                        dto.getTotalCost()
                );
                tblReservation.setItems(FXCollections.observableArrayList(tm));
            } else {
                tblReservation.setItems(FXCollections.observableArrayList());
                new Alert(Alert.AlertType.INFORMATION, "No reservation found with ID: " + searchText).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to search reservation: " + e.getMessage()).show();
        }
    }
}

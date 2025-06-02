package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.ReservationDTO;
import lk.ijse.hotelmanagementsystem.model.ReservationModel;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {
    public Label lblReservationId;
    public DatePicker dpCheckInDate;
    public TextField txtBookingTime;
    public DatePicker dpCheckOutDate;
    public ComboBox cmStatus;
    public TextField txtTotalCost;
    public ComboBox cmGuestId;
    public ComboBox cmRoomId;
    public TextField txtNumberofGuest;
    public Button btnCancel;
    public Button btnSave;

    private boolean isEditMode = false;
    private ReservationModel reservationModel = new ReservationModel();
    private String reservationIDValidation = "^RES\\d{3}$";

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "❌ Failed to logout").show();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmStatus.getItems().addAll("Pending", "Confirmed", "Checked In", "Checked Out", "Cancelled");
        try {
            lblReservationId.setText(reservationModel.getNextReservationId());
            cmGuestId.getItems().addAll(reservationModel.loadGuestIds());
            cmRoomId.getItems().addAll(reservationModel.loadRoomIds());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel and reset the form?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            resetPage();
        }
    }

    private void resetPage() {
        try {
            loadNextId();

            dpCheckInDate.setValue(null);
            dpCheckOutDate.setValue(null);
            txtBookingTime.clear();
            txtNumberofGuest.clear();
            txtTotalCost.clear();
            cmStatus.getSelectionModel().clearSelection();
            cmGuestId.getSelectionModel().clearSelection();
            cmRoomId.getSelectionModel().clearSelection();
            btnSave.setDisable(false);
            btnCancel.setDisable(false);
            isEditMode = false; // Ensure edit mode is off for new reservations
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Something went wrong while resetting the page").show();
        }
    }

    private void loadNextId() throws SQLException, ClassNotFoundException {
        String nextId = ReservationModel.getNextReservationId();
        lblReservationId.setText(nextId);
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        try {
            String reservationID = lblReservationId.getText();
            String guestID = cmGuestId.getSelectionModel().getSelectedItem().toString();
            String roomID = cmRoomId.getSelectionModel().getSelectedItem().toString();
            Date checkInDate = Date.valueOf(dpCheckInDate.getValue());
            Date checkOutDate = Date.valueOf(dpCheckOutDate.getValue());
            String bookingTimeStr = txtBookingTime.getText();
            int numberOfGuests = Integer.parseInt(txtNumberofGuest.getText());
            String status = cmStatus.getSelectionModel().getSelectedItem().toString();
            double totalCost = Double.parseDouble(txtTotalCost.getText());

            if (!reservationID.matches(reservationIDValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid Reservation ID format (e.g., RES001)").show();
                return;
            }

            // Combine check-in date and booking time (e.g., 14:30)
            String timeText = txtBookingTime.getText(); // e.g., "14:30"
            if (!timeText.matches("^\\d{2}:\\d{2}$")) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid time format. Use HH:mm (e.g., 14:30)").show();
                return;
            }

            String fullTimestamp = dpCheckInDate.getValue().toString() + " " + timeText + ":00"; // add seconds
            Timestamp bookingTime = Timestamp.valueOf(fullTimestamp);


            ReservationDTO reservationDTO = new ReservationDTO(
                    reservationID,
                    guestID,
                    roomID,
                    checkInDate,
                    checkOutDate,
                    bookingTime,
                    numberOfGuests,
                    status,
                    totalCost
            );

            boolean success;
            if (isEditMode) {
                success = reservationModel.updateReservation(reservationDTO);
            } else {
                success = reservationModel.saveReservation(reservationDTO);
            }

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, isEditMode ? "✅ Updated successfully" : "✅ Saved successfully").show();
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "❌ Operation failed").show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Error: " + e.getMessage()).show();
        }
    }

    public void setReservationData(ReservationDTO dto) {
        isEditMode = true;
        lblReservationId.setText(dto.getReservationId());
        cmGuestId.setValue(dto.getGuestId());
        cmRoomId.setValue(dto.getRoomId());
        dpCheckInDate.setValue(dto.getCheckInDate().toLocalDate());
        dpCheckOutDate.setValue(dto.getCheckOutDate().toLocalDate());
        txtBookingTime.setText(dto.getBookingTime());
        txtNumberofGuest.setText(String.valueOf(dto.getNumberOfGuests()));
        cmStatus.setValue(dto.getStatus());
        txtTotalCost.setText(String.valueOf(dto.getTotalCost()));
    }
}

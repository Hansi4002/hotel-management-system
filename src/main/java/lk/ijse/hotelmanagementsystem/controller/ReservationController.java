package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationController implements Initializable {
    public Label lblReservationId;
    public DatePicker dpCheckInDate;
    public TextField txtBookingTime;
    public DatePicker dpCheckOutDate;
    public ComboBox<String> cmStatus;
    public TextField txtTotalCost;
    public ComboBox<String> cmGuestId;
    public ComboBox<String> cmRoomId;
    public TextField txtNumberofGuest;
    public Button btnCancel;
    public Button btnSave;
    public Button btnLogout;

    private boolean isEditMode = false;
    private ReservationModel reservationModel = new ReservationModel();
    private String reservationIDValidation = "^RES\\d{3}$";
    private ReservationTableController reservationTableController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmStatus.setItems(FXCollections.observableArrayList("Pending", "Confirmed", "Checked In", "Checked Out", "Cancelled"));
        try {
            String nextId = reservationModel.getNextReservationId();
            if (nextId == null || !nextId.matches(reservationIDValidation)) {
                throw new SQLException("Invalid reservation ID generated: " + nextId);
            }
            lblReservationId.setText(nextId);
            cmGuestId.setItems(FXCollections.observableArrayList(reservationModel.loadGuestIds()));
            cmRoomId.setItems(FXCollections.observableArrayList(reservationModel.loadRoomIds()));
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to initialize reservation data: " + e.getMessage()).show();
            lblReservationId.setText("ERROR");
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        try {
            String reservationId = lblReservationId.getText();
            String guestId = cmGuestId.getSelectionModel().getSelectedItem();
            String roomId = cmRoomId.getSelectionModel().getSelectedItem();
            java.time.LocalDate checkInLocal = dpCheckInDate.getValue();
            java.time.LocalDate checkOutLocal = dpCheckOutDate.getValue();
            String bookingTimeStr = txtBookingTime.getText();
            String numberOfGuestsStr = txtNumberofGuest.getText();
            String status = cmStatus.getSelectionModel().getSelectedItem();
            String totalCostStr = txtTotalCost.getText();

            if (reservationId.isEmpty() || guestId == null || roomId == null || checkInLocal == null ||
                    checkOutLocal == null || bookingTimeStr.isEmpty() || numberOfGuestsStr.isEmpty() ||
                    status == null || totalCostStr.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
                return;
            }

            if (!reservationId.matches(reservationIDValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid Reservation ID format (e.g., RES001): " + reservationId).show();
                return;
            }

            if (!bookingTimeStr.matches("^\\d{2}:\\d{2}$")) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid time format. Use HH:mm (e.g., 14:30)").show();
                return;
            }

            int numberOfGuests;
            double totalCost;
            try {
                numberOfGuests = Integer.parseInt(numberOfGuestsStr);
                if (numberOfGuests <= 0) {
                    new Alert(Alert.AlertType.ERROR, "❌ Number of guests must be positive").show();
                    return;
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid number of guests").show();
                return;
            }

            try {
                totalCost = Double.parseDouble(totalCostStr);
                if (totalCost < 0) {
                    new Alert(Alert.AlertType.ERROR, "❌ Total cost cannot be negative").show();
                    return;
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid total cost").show();
                return;
            }

            if (checkInLocal.isAfter(checkOutLocal)) {
                new Alert(Alert.AlertType.ERROR, "❌ Check-in date must be before check-out date").show();
                return;
            }

            Date checkInDate = Date.valueOf(checkInLocal);
            Date checkOutDate = Date.valueOf(checkOutLocal);

            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fullTimestamp = checkInLocal.toString() + " " + bookingTimeStr + ":00";
            Timestamp bookingTime;
            try {
                bookingTime = new Timestamp(dateTimeFormat.parse(fullTimestamp).getTime());
            } catch (ParseException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid booking time format").show();
                return;
            }

            ReservationDTO reservationDTO = new ReservationDTO(
                    reservationId,
                    guestId,
                    roomId,
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
                if (reservationTableController != null) {
                    reservationTableController.loadReservationData();
                }
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "❌ Operation failed").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Database error: " + e.getMessage()).show();
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
            String nextId = reservationModel.getNextReservationId();
            if (nextId == null || !nextId.matches(reservationIDValidation)) {
                throw new SQLException("Invalid reservation ID generated: " + nextId);
            }
            lblReservationId.setText(nextId);
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
            isEditMode = false;
            lblReservationId.setDisable(false);
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to reset form: " + e.getMessage()).show();
            lblReservationId.setText("ERROR");
        }
    }

    public void setReservationData(ReservationDTO dto) {
        isEditMode = true;
        if (dto != null) {
            String resId = dto.getReservationId();
            if (resId == null || !resId.matches(reservationIDValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid reservation ID in edit mode: " + resId).show();
                return;
            }
            lblReservationId.setText(resId);
            cmGuestId.setValue(dto.getGuestId());
            cmRoomId.setValue(dto.getRoomId());
            dpCheckInDate.setValue(dto.getCheckInDate().toLocalDate());
            dpCheckOutDate.setValue(dto.getCheckOutDate().toLocalDate());
            txtBookingTime.setText(new SimpleDateFormat("HH:mm").format(dto.getBookingTime()));
            txtNumberofGuest.setText(String.valueOf(dto.getNumberOfGuests()));
            cmStatus.setValue(dto.getStatus());
            txtTotalCost.setText(String.valueOf(dto.getTotalCost()));
            lblReservationId.setDisable(true);
        } else {
            new Alert(Alert.AlertType.ERROR, "❌ No reservation data provided").show();
        }
    }

    public void setReservationTableController(ReservationTableController reservationTableController) {
        this.reservationTableController = reservationTableController;
    }
}
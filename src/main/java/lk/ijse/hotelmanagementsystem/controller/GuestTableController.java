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
import lk.ijse.hotelmanagementsystem.dto.GuestDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.GuestTM;
import lk.ijse.hotelmanagementsystem.model.GuestModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class GuestTableController implements Initializable {
    public Hyperlink linkProfile;
    public Hyperlink linkReservations;
    public Hyperlink linkOrders;
    public Hyperlink linkReviews;
    public Button btnLogout;
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddGuest;
    public TableView<GuestTM> tblGuests;
    public TableColumn<GuestTM, String> colGuestId;
    public TableColumn<GuestTM, String> colName;
    public TableColumn<GuestTM, Date> colDOB;
    public TableColumn<GuestTM, String> colAddress;
    public TableColumn<GuestTM, String> colContact;
    public TableColumn<GuestTM, String> colEmail;
    public TableColumn<GuestTM, Date> colRegistrationDate;
    public TableColumn<GuestTM, String> colLoyaltyStatus;

    private final GuestModel guestModel = new GuestModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colGuestId.setCellValueFactory(new PropertyValueFactory<>("guestId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colLoyaltyStatus.setCellValueFactory(new PropertyValueFactory<>("loyaltyStatus"));

        tblGuests.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loadGuestData();
    }

    void loadGuestData() {
        try {
            List<GuestDTO> guestList = guestModel.getAllGuests();
            List<GuestTM> guestTMs = guestList.stream().map(dto -> new GuestTM(
                    dto.getGuestId(),
                    dto.getName(),
                    dto.getDob(),
                    dto.getAddress(),
                    dto.getContact(),
                    dto.getEmail(),
                    dto.getRegistrationDate(),
                    dto.getLoyaltyStatus()
            )).toList();

            ObservableList<GuestTM> observableList = FXCollections.observableArrayList(guestTMs);
            tblGuests.setItems(observableList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load guest data").show();
        }
    }

    public void btnAddNewGuestOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GuestView.fxml"));
            Parent load = loader.load();
            GuestController controller = loader.getController();
            controller.setGuestTableController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Guest");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Add Guest window").show();
        }
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        GuestTM selectedGuest = tblGuests.getSelectionModel().getSelectedItem();

        if (selectedGuest == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a guest to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GuestView.fxml"));
            Parent load = loader.load();

            GuestController controller = loader.getController();
            controller.setGuestData(new GuestDTO(
                    selectedGuest.getGuestId(),
                    selectedGuest.getName(),
                    selectedGuest.getDob(),
                    selectedGuest.getAddress(),
                    selectedGuest.getContact(),
                    selectedGuest.getEmail(),
                    selectedGuest.getRegistrationDate(),
                    selectedGuest.getLoyaltyStatus()
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Guest");
            stage.show();

            stage.setOnHiding(event -> loadGuestData());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Edit Guest window").show();
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        GuestTM selectedGuest = tblGuests.getSelectionModel().getSelectedItem();

        if (selectedGuest == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a guest to delete").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete guest " + selectedGuest.getGuestId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean isDeleted = guestModel.deleteGuest(selectedGuest.getGuestId());
                    if (isDeleted) {
                        tblGuests.getItems().remove(selectedGuest);
                        new Alert(Alert.AlertType.INFORMATION, "Guest deleted successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete guest").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Error deleting guest: " + e.getMessage()).show();
                }
            }
        });
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        stage.close();
    }

    public void goToProfile(ActionEvent actionEvent) {
        loadPage("/view/UserProfile.fxml", "Profile");
    }

    public void goToReservations(ActionEvent actionEvent) {
        loadPage("/view/Reservation.fxml", "Reservations");
    }

    public void goToOrders(ActionEvent actionEvent) {
        loadPage("/view/FoodOrder.fxml", "Food Orders");
    }

    public void goToReviews(ActionEvent actionEvent) {
        loadPage("/view/Feedback.fxml", "Feedback");
    }

    private void loadPage(String path, String title) {
        try {
            Parent load = FXMLLoader.load(getClass().getResource(path));
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Page Load Failed!").show();
        }
    }
}
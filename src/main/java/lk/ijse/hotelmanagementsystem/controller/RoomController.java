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
import lk.ijse.hotelmanagementsystem.dto.RoomDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.RoomTM;
import lk.ijse.hotelmanagementsystem.model.RoomModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class RoomController implements Initializable {
    public Button btnLogout;
    public Button btnAddRoom;
    public TableView<RoomTM> tblRoom;
    public TableColumn<RoomTM, String> colRoomId;
    public TableColumn<RoomTM, String> colRoomType;
    public TableColumn<RoomTM, Double> colPrice;
    public TableColumn<RoomTM, String> colStatus;
    public TableColumn<RoomTM, String> colFloorNumber;
    public TableColumn<RoomTM, Integer> colCapacity;
    public TableColumn<RoomTM, String> colDescription;
    public TableColumn<RoomTM, String> colRoomNumber;
    public Hyperlink linkProfile;
    public Hyperlink linkReservations;
    public Hyperlink linkOrders;
    public Hyperlink linkReviews;
    public Button btnEdit;

    private final RoomModel roomModel = new RoomModel();
    private final RoomDTO roomDTO = new RoomDTO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colFloorNumber.setCellValueFactory(new PropertyValueFactory<>("floorNumber"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        tblRoom.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loadRoomData();
    }

    private void loadRoomData() {
        try {
            List<RoomDTO> roomList = roomModel.getAllRooms();
            List<RoomTM> roomTMs = roomList.stream().map(dto -> new RoomTM(
                    dto.getRoomId(),
                    dto.getRoomType(),
                    dto.getPrice(),
                    dto.getStatus(),
                    dto.getFloorNumber(),
                    dto.getCapacity(),
                    dto.getDescription(),
                    dto.getRoomNumber()
            )).toList();

            ObservableList<RoomTM> observableList = FXCollections.observableArrayList(roomTMs);
            tblRoom.setItems(observableList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load room data").show();
        }
    }

    public void btnAddNewRoomOnAction(ActionEvent actionEvent) {
        try {
            Parent load = FXMLLoader.load(getClass().getResource("/view/RoomDetailView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Room");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void btnCancelOnAction(ActionEvent actionEvent) {
        RoomTM selectedRoom = tblRoom.getSelectionModel().getSelectedItem();

        if (selectedRoom == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a room to delete").show();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete room " + selectedRoom.getRoomId() + "?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    // Delete the room from the database
                    boolean isDeleted = roomModel.deleteRoom(selectedRoom.getRoomId());
                    if (isDeleted) {
                        // Remove the room from the TableView
                        tblRoom.getItems().remove(selectedRoom);
                        new Alert(Alert.AlertType.INFORMATION, "Room deleted successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete room").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Error deleting room: " + e.getMessage()).show();
                }
            }
        });
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        RoomTM selectedRoom = tblRoom.getSelectionModel().getSelectedItem();

        if (selectedRoom == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a room to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RoomDetailView.fxml"));
            Parent load = loader.load();

            RoomDetailController controller = loader.getController();

            controller.setRoomData(new RoomDTO(
                    selectedRoom.getRoomId(),
                    selectedRoom.getRoomType(),
                    selectedRoom.getPrice(),
                    selectedRoom.getStatus(),
                    selectedRoom.getFloorNumber(),
                    selectedRoom.getCapacity(),
                    selectedRoom.getDescription(),
                    selectedRoom.getRoomNumber()
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Room");
            stage.show();

            stage.setOnHiding(event -> loadRoomData());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Edit Room window").show();
        }
    }

}
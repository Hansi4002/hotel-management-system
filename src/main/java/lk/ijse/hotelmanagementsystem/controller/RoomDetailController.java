package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.hotelmanagementsystem.dto.RoomDTO;
import lk.ijse.hotelmanagementsystem.model.RoomModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class RoomDetailController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmRoomType.getItems().addAll("Single", "Suite", "Double");
        cmStatus.getItems().addAll("Available", "Occupied", "Maintenance","Under Maintenance");
        cmFloorNumber.getItems().addAll("1", "2", "3");
        cmCapacity.getItems().addAll("1", "2", "3", "4","5","6","7","8","9");

        try {
            lblRoomId.setText(RoomModel.getNextRoomId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void loadNextId() throws SQLException, ClassNotFoundException {
//        String nextId = RoomModel.getNextRoomId();
//        lblRoomId.setText(nextId);
//    }

    public Button btnLogout;
    public Button btnEdit;
    public Label lblRoomId;
    public ComboBox cmRoomType;
    public TextField txtPrice;
    public ComboBox cmStatus;
    public ComboBox cmFloorNumber;
    public ComboBox cmCapacity;
    public TextField txtRoomNumber;
    public TextField txtDescription;
    public Button btnCancel;
    public Button btnSave;
    private RoomModel roomModel = new RoomModel();
    private String roomIDValidation = "^R\\d{3}$";

    public void btnLogoutOnAction(ActionEvent actionEvent) {
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
//        String roomId = lblRoomId.getText();
//        String roomType = (String) cmRoomType.getSelectionModel().getSelectedItem();
//        double price = Double.parseDouble(txtPrice.getText());
//        String status = (String) cmStatus.getSelectionModel().getSelectedItem();;
//        String floorNumber = (String) cmFloorNumber.getSelectionModel().getSelectedItem();
//        int capacity = Integer.parseInt(cmCapacity.getSelectionModel().getSelectedItem().toString());
//        String description = txtDescription.getText();
//        String roomNumber = txtRoomNumber.getText();
//
//        RoomDTO roomDTO = new RoomDTO(roomId, roomType, price, status, floorNumber, capacity, description, roomNumber);
//
//        try {
//            boolean isUpdated = roomModel.updateRoom(roomDTO);
//            if (isUpdated) {
//                resetPage();
//                new Alert(Alert.AlertType.INFORMATION, "Room updated successfully").show();
//            }else {
//                new Alert(Alert.AlertType.ERROR, "Room update failed").show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR, "Something went wrong when trying to update room").show();
//        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.YES, ButtonType.CANCEL);
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.YES){
//            String roomId = lblRoomId.getText();
//            try {
//                boolean isDeleted = roomModel.deleteRoom(roomId);
//                if (isDeleted){
//                    resetPage();
//                    new Alert(Alert.AlertType.INFORMATION, "Room Deleted successfully").show();
//                }else {
//                    new Alert(Alert.AlertType.ERROR, "Room Delete failed").show();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//                new Alert(Alert.AlertType.ERROR, "Something went wrong when trying to delete room").show();
//            }
//        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            String roomId = lblRoomId.getText();
            String roomType = (String) cmRoomType.getSelectionModel().getSelectedItem();
            String priceText = txtPrice.getText();
            String status = (String) cmStatus.getSelectionModel().getSelectedItem();
            String floorNumber = (String) cmFloorNumber.getSelectionModel().getSelectedItem();
            Object capacityObj = cmCapacity.getSelectionModel().getSelectedItem();
            String description = txtDescription.getText();
            String roomNumber = txtRoomNumber.getText();

            // 🔎 Basic validation
            if (roomId.isEmpty() || roomType == null || priceText.isEmpty() || status == null ||
                    floorNumber == null || capacityObj == null || roomNumber.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
                return;
            }

            double price;
            int capacity;

            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid price").show();
                return;
            }

            try {
                capacity = Integer.parseInt(capacityObj.toString());
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid capacity").show();
                return;
            }

            RoomDTO roomDTO = new RoomDTO(
                    roomId,
                    roomType,
                    price,
                    status,
                    floorNumber,
                    capacity,
                    description,
                    roomNumber
            );

            // ✅ Debug
            System.out.println("🔧 Saving room: " + roomDTO);

            // ❗ TEMPORARILY COMMENT OUT VALIDATION CHECK IF NEEDED
            // if (roomIDValidation.equals(roomId)) {
            boolean isSaved = roomModel.saveRoom(roomDTO);
            if (isSaved) {
//                resetPage();
                new Alert(Alert.AlertType.INFORMATION, "✅ Room saved successfully").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "❌ Room saving failed").show();
            }
            // }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Something went wrong trying to save the room").show();
        }

    private void resetPage() {
        try {
            loadNextId();
            btnSave.setDisable(true);
            btnCancel.setDisable(true);
            btnEdit.setDisable(false);

            cmRoomType.setValue(null);
            txtPrice.setText("");
            cmStatus.setValue(null);
            cmFloorNumber.setValue(null);
            cmCapacity.setValue(null);
            txtDescription.setText("");
            txtRoomNumber.setText("");
        }catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Something went wrong while resetting the page").show();
        }
    }

    private void loadNextId() throws SQLException, ClassNotFoundException {
        String nextId = RoomModel.getNextRoomId();
        lblRoomId.setText(nextId);
    }
    }}
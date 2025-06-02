package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.RoomDTO;
import lk.ijse.hotelmanagementsystem.model.RoomModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;

import java.util.ResourceBundle;

public class RoomDetailController implements Initializable {
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
    private boolean isEditMode = false;
    private String existingRoomId = null;
    private RoomController roomController;

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

    private void resetPage() {
        try {
            loadNextId();

            cmRoomType.getSelectionModel().clearSelection();
            txtPrice.clear();
            cmStatus.getSelectionModel().clearSelection();
            cmFloorNumber.getSelectionModel().clearSelection();
            cmCapacity.getSelectionModel().clearSelection();
            txtDescription.clear();
            txtRoomNumber.clear();

            btnSave.setDisable(false);
            btnCancel.setDisable(false);
            if (btnEdit != null) {
                btnEdit.setDisable(false);
            } else {
                System.err.println("Warning: btnEdit is null. Check your FXML for fx:id='btnEdit'");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Something went wrong while resetting the page").show();
        }
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

            if (!roomId.matches(roomIDValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid Room ID format (e.g., R001)").show();
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

            if (isEditMode) {
                boolean isUpdated = roomModel.updateRoom(roomDTO);
                if (isUpdated) {
                    new Alert(Alert.AlertType.INFORMATION, "✅ Room updated successfully").show();
                    Stage stage = (Stage) btnSave.getScene().getWindow();
                    stage.close();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed to update room").show();
                }
            } else {
                boolean isSaved = roomModel.saveRoom(roomDTO);
                if (isSaved) {
                    resetPage();
                    new Alert(Alert.AlertType.INFORMATION, "✅ Room saved successfully").show();
                    if (roomController != null) {
                        roomController.loadRoomData();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Room saving failed").show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Something went wrong trying to save the room").show();
        }
    }

    private void loadNextId() throws SQLException, ClassNotFoundException {
        String nextId = RoomModel.getNextRoomId();
        lblRoomId.setText(nextId);
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel and reset the form?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            resetPage();
        }
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        if (!isEditMode) {
            new Alert(Alert.AlertType.WARNING, "⚠ Edit mode not active. Please select a room to edit.").show();
            return;
        }

        try {
            String roomId = lblRoomId.getText();
            String roomType = cmRoomType.getSelectionModel().getSelectedItem().toString();
            String priceText = txtPrice.getText();
            String status = cmStatus.getSelectionModel().getSelectedItem().toString();
            String floorNumber = cmFloorNumber.getSelectionModel().getSelectedItem().toString();
            String capacityText = cmCapacity.getSelectionModel().getSelectedItem().toString();
            String description = txtDescription.getText();
            String roomNumber = txtRoomNumber.getText();

            if (roomId.isEmpty() || roomType == null || priceText.isEmpty() || status == null ||
                    floorNumber == null || capacityText == null || roomNumber.isEmpty()) {
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
                capacity = Integer.parseInt(capacityText);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid capacity").show();
                return;
            }

            if (!roomId.matches(roomIDValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid Room ID format (e.g., R001)").show();
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

            boolean isUpdated = roomModel.updateRoom(roomDTO);
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "✅ Room updated successfully").show();
                if (roomController != null) {
                    roomController.loadRoomData();
                }
                Stage stage = (Stage) btnEdit.getScene().getWindow();
                stage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "❌ Failed to update room").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Error updating room: " + e.getMessage()).show();
        }
    }

    public void setRoomData(RoomDTO roomDTO) {
        isEditMode = true;
        existingRoomId = roomDTO.getRoomId();
        if (roomDTO != null) {
            lblRoomId.setText(roomDTO.getRoomId());
            cmRoomType.setValue(roomDTO.getRoomType());
            txtPrice.setText(String.valueOf(roomDTO.getPrice()));
            cmStatus.setValue(roomDTO.getStatus());
            cmFloorNumber.setValue(roomDTO.getFloorNumber());
            cmCapacity.setValue(roomDTO.getCapacity());
            txtDescription.setText(roomDTO.getDescription());
            txtRoomNumber.setText(roomDTO.getRoomNumber());

            txtPrice.setDisable(false);
            cmRoomType.setDisable(false);
            cmStatus.setDisable(false);
            cmFloorNumber.setDisable(false);
            cmCapacity.setDisable(false);
            txtDescription.setDisable(false);
            txtRoomNumber.setDisable(false);

            btnSave.setDisable(false);
            if (btnEdit != null) {
                btnEdit.setDisable(true);
            } else {
                System.err.println("Warning: btnEdit is null. Check FXML file for fx:id='btnEdit'.");
            }
            btnCancel.setDisable(false);
        } else {
            new Alert(Alert.AlertType.ERROR, "❌ No room data provided").show();
        }
        lblRoomId.setDisable(true);
    }

    public void setRoomController(RoomController roomController) {
        this.roomController = roomController;
    }
}
package lk.ijse.hotelmanagementsystem.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.FoodDTO;
import lk.ijse.hotelmanagementsystem.model.FoodModel;
import lombok.SneakyThrows;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class FoodController implements Initializable {
    public Label lblMenuId;
    public ComboBox<String> cmAvailable;
    public ComboBox<String> cmCategory;
    public TextField txtPrice;
    public TextField txtItemName;
    public TextField txtDescription;
    public Button btnCancel;
    public Button btnSave;

    private FoodModel foodModel = new FoodModel();
    private FoodTableController foodTableController;
    private boolean isEditMode = false;
    private String existingMenuId = null;
    private String menuIdValidation = "^F\\d{3}$";
    private String priceValidation = "^\\d+(\\.\\d{1,2})?$";
    private String itemNameValidation = "^[A-Za-z0-9\\s]+$";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmAvailable.setItems(FXCollections.observableArrayList("Available", "Not Available"));
        cmCategory.setItems(FXCollections.observableArrayList("Main Course", "Dessert", "Beverage", "Appetizer"));

        try {
            lblMenuId.setText(FoodModel.getNextMenuId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel and reset the form?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                resetPage();
            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, "❌ Error resetting form: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        try {
            String menuId = lblMenuId.getText();
            String available = cmAvailable.getSelectionModel().getSelectedItem();
            String category = cmCategory.getSelectionModel().getSelectedItem();
            String priceText = txtPrice.getText();
            String itemName = txtItemName.getText();
            String description = txtDescription.getText();

            if (menuId.isEmpty() || available == null || category == null ||
                    priceText.isEmpty() || itemName.isEmpty() || description.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
                return;
            }

            if (!menuId.matches(menuIdValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid Menu ID format (e.g., F001)").show();
                return;
            }

            if (!itemName.matches(itemNameValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Item name can only contain letters, numbers, and spaces").show();
                return;
            }

            if (!priceText.matches(priceValidation)) {
                new Alert(Alert.AlertType.ERROR, "❌ Invalid price format (e.g., 10.99)").show();
                return;
            }

            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                new Alert(Alert.AlertType.ERROR, "❌ Price must be a positive number").show();
                return;
            }

            FoodDTO foodDTO = new FoodDTO(menuId,available,category,price,itemName,description);

            if (isEditMode) {
                boolean isUpdated = foodModel.updateFood(foodDTO);
                if (isUpdated) {
                    new Alert(Alert.AlertType.INFORMATION, "✅ Food item updated successfully").show();
                    if (foodTableController != null) {
                        foodTableController.loadFoodData();
                    }
                    Stage stage = (Stage) btnSave.getScene().getWindow();
                    stage.close();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed to update food item").show();
                }
            } else {
                boolean isSaved = foodModel.saveFood(foodDTO);
                if (isSaved) {
                    new Alert(Alert.AlertType.INFORMATION, "✅ Food item saved successfully").show();
                    resetPage();
                    if (foodTableController != null) {
                        foodTableController.loadFoodData();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed to save food item").show();
                }
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid price format").show();
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Error saving food item: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private void resetPage() throws SQLException, ClassNotFoundException {
        lblMenuId.setText(FoodModel.getNextMenuId());
        cmAvailable.getSelectionModel().clearSelection();
        cmCategory.getSelectionModel().clearSelection();
        txtPrice.clear();
        txtItemName.clear();
        txtDescription.clear();
        btnSave.setDisable(false);
        btnCancel.setDisable(false);
        isEditMode = false;
        existingMenuId = null;
        lblMenuId.setDisable(false);
    }

    public void setFoodData(FoodDTO foodDTO) {
        isEditMode = true;
        existingMenuId = foodDTO.getMenuId();
        if (foodDTO != null) {
            lblMenuId.setText(foodDTO.getMenuId());
            txtItemName.setText(foodDTO.getItemName());
            txtDescription.setText(foodDTO.getDescription());
            cmCategory.setValue(foodDTO.getCategory());
            cmAvailable.setValue(foodDTO.getAvailable());
            txtPrice.setText(String.valueOf(foodDTO.getPrice()));

            txtItemName.setDisable(false);
            txtDescription.setDisable(false);
            cmCategory.setDisable(false);
            cmAvailable.setDisable(false);
            txtPrice.setDisable(false);
            btnSave.setDisable(false);
            btnCancel.setDisable(false);
            lblMenuId.setDisable(true);
        } else {
            new Alert(Alert.AlertType.ERROR, "❌ No food data provided").show();
        }
    }

    public void setFoodTableController(FoodTableController foodTableController) {
        this.foodTableController = foodTableController;
    }
}
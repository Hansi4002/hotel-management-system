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
import lk.ijse.hotelmanagementsystem.dto.FoodDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.FoodTM;
import lk.ijse.hotelmanagementsystem.model.FoodModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FoodTableController implements Initializable {
    public TableView<FoodTM> tblFood;
    public TableColumn<FoodTM, String> colMenuId;
    public TableColumn<FoodTM, String> colAvailable;
    public TableColumn<FoodTM, String> colCategory;
    public TableColumn<FoodTM, String> colItemName;
    public TableColumn<FoodTM, String> colDescription;
    public TableColumn<FoodTM, Double> colPrice;
    public Button btnCancel;
    public Button btnEdit;

    private final FoodModel foodModel = new FoodModel();
    public Button btnAddFood;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colMenuId.setCellValueFactory(new PropertyValueFactory<>("menuId"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        tblFood.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        loadFoodData();
    }

    public void loadFoodData() {
        try {
            List<FoodDTO> foodList = foodModel.getAllFoods();
            List<FoodTM> foodTMs = foodList.stream().map(dto -> new FoodTM(
                    dto.getMenuId(),
                    dto.getItemName(),
                    dto.getDescription(),
                    dto.getCategory(),
                    dto.getAvailable(),
                    dto.getPrice()
            )).toList();

            ObservableList<FoodTM> observableList = FXCollections.observableArrayList(foodTMs);
            tblFood.setItems(observableList);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load food data").show();
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        FoodTM selectedFood = tblFood.getSelectionModel().getSelectedItem();

        if (selectedFood == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a food item to delete").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete food item " + selectedFood.getItemName() + " (" + selectedFood.getMenuId() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    boolean isDeleted = foodModel.deleteFood(selectedFood.getMenuId());
                    if (isDeleted) {
                        tblFood.getItems().remove(selectedFood);
                        new Alert(Alert.AlertType.INFORMATION, "✅ Food item deleted successfully").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "❌ Failed to delete food item").show();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "❌ Error deleting food item: " + e.getMessage()).show();
                }
            }
        });
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        FoodTM selectedFood = tblFood.getSelectionModel().getSelectedItem();

        if (selectedFood == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a food item to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FoodView.fxml"));
            Parent load = loader.load();

            FoodController controller = loader.getController();
            controller.setFoodData(new FoodDTO(
                    selectedFood.getMenuId(),
                    selectedFood.getItemName(),
                    selectedFood.getDescription(),
                    selectedFood.getCategory(),
                    selectedFood.getAvailable(),
                    selectedFood.getPrice()
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Food");
            stage.show();

            stage.setOnHiding(event -> loadFoodData());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Edit Food window").show();
        }
    }

    public void btnAddNewFoodOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FoodView.fxml"));
            Parent load = loader.load();
            FoodController controller = loader.getController();
            controller.setFoodTableController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Food");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Add Food window").show();
        }
    }
}
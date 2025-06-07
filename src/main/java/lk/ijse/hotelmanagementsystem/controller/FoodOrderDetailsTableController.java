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
import lk.ijse.hotelmanagementsystem.dto.FoodOrderDetailDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.FoodOrderDetailsTM;
import lk.ijse.hotelmanagementsystem.model.FoodOrderDetailsModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FoodOrderDetailsTableController implements Initializable {
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddFoodOrder;
    public TableView<FoodOrderDetailsTM> tblFoodOrderDetail;
    public TableColumn<FoodOrderDetailsTM, String> colMenuId;
    public TableColumn<FoodOrderDetailsTM, String> colOrderId;
    public TableColumn<FoodOrderDetailsTM, Double> colItemPrice;
    public TableColumn<FoodOrderDetailsTM, Integer> colQuantity;
    public TableColumn<FoodOrderDetailsTM, Double> colTotalPrice;

    private final FoodOrderDetailsModel foodOrderDetailsModel = new FoodOrderDetailsModel();
    public TextField txtSearch;
    public Button btnSearch;

    public void btnCancelOnAction(ActionEvent actionEvent) {
        FoodOrderDetailsTM selectedDetail = tblFoodOrderDetail.getSelectionModel().getSelectedItem();

        if (selectedDetail == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a food order detail to cancel").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel detail for Menu ID " + selectedDetail.getMenuId() +
                        " and Order ID " + selectedDetail.getOrderId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean isDeleted = false;
                try {
                    isDeleted = foodOrderDetailsModel.deleteFoodOrderDetail(
                            selectedDetail.getMenuId(), selectedDetail.getOrderId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (isDeleted) {
                    tblFoodOrderDetail.getItems().remove(selectedDetail);
                    new Alert(Alert.AlertType.INFORMATION, "✅ Food order detail cancelled successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed to cancel food order detail").show();
                }
            }
        });
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        FoodOrderDetailsTM selectedDetail = tblFoodOrderDetail.getSelectionModel().getSelectedItem();

        if (selectedDetail == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a food order detail to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FoodOrderDetailView.fxml"));
            Parent load = loader.load();

            FoodOrderDetailController controller = loader.getController();
            controller.setFoodOrderDetailData(new FoodOrderDetailDTO(
                    selectedDetail.getMenuId(),
                    selectedDetail.getOrderId(),
                    (Double) selectedDetail.getItemPrice(),
                    selectedDetail.getQuantity()
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Food Order Detail");
            stage.show();

            stage.setOnHiding(event -> {
                try {
                    loadFoodOrderDetailsData();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Edit Food Order Detail window: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnAddNewFoodOrderOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FoodOrderDetailView.fxml"));
            Parent load = loader.load();
            FoodOrderDetailController controller = loader.getController();
            controller.setFoodOrderDetailsTableController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Food Order Detail");
            stage.show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Add Food Order Detail window: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void loadFoodOrderDetailsData() throws SQLException {
        List<FoodOrderDetailDTO> detailList = foodOrderDetailsModel.getAllFoodOrderDetails();
        List<FoodOrderDetailsTM> detailTMs = detailList.stream().map(dto -> new FoodOrderDetailsTM(
                dto.getMenuId(),
                dto.getOrderId(),
                dto.getItemPrice(),
                dto.getQuantity()
        )).toList();

        ObservableList<FoodOrderDetailsTM> observableList = FXCollections.observableArrayList(detailTMs);
        tblFoodOrderDetail.setItems(observableList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colMenuId.setCellValueFactory(new PropertyValueFactory<>("menuId"));
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colItemPrice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        tblFoodOrderDetail.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        try {
            loadFoodOrderDetailsData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnSearchOnAction(ActionEvent actionEvent) throws SQLException {
        String keyword = txtSearch.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadFoodOrderDetailsData();
            return;
        }

        ObservableList<FoodOrderDetailsTM> filteredList = FXCollections.observableArrayList();

        for (FoodOrderDetailsTM detail : tblFoodOrderDetail.getItems()) {
            if (
                    detail.getMenuId().toLowerCase().contains(keyword) ||
                            detail.getOrderId().toLowerCase().contains(keyword) ||
                            String.valueOf(detail.getItemPrice()).contains(keyword) ||
                            String.valueOf(detail.getQuantity()).contains(keyword)
            ) {
                filteredList.add(detail);
            }
        }

        tblFoodOrderDetail.setItems(filteredList);

        if (filteredList.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "🔍 No matching food order details found.").show();
        }
    }
}
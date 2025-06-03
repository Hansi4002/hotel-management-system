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
import lk.ijse.hotelmanagementsystem.dto.FoodOrderDTO;
import lk.ijse.hotelmanagementsystem.dto.tm.FoodOrderTM;
import lk.ijse.hotelmanagementsystem.model.FoodOrderModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FoodOrderTableController implements Initializable {
    public TableView<FoodOrderTM> tblFoodOrder;
    public TableColumn<FoodOrderTM, String> colOrderId;
    public TableColumn<FoodOrderTM, String> colReservationID;
    public TableColumn<FoodOrderTM, String> colOrderType;
    public TableColumn<FoodOrderTM, String> colStatus;
    public TableColumn<FoodOrderTM, Double> colTotalAmount;
    public TableColumn<FoodOrderTM, String> colOrderDate;
    public Button btnCancel;
    public Button btnEdit;
    public Button btnAddFoodOrder;

    private final FoodOrderModel foodOrderModel = new FoodOrderModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colReservationID.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        colOrderType.setCellValueFactory(new PropertyValueFactory<>("orderType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        tblFoodOrder.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        loadFoodOrderData();
    }

    public void loadFoodOrderData() {
        try {
            List<FoodOrderDTO> orderList = foodOrderModel.getAllFoodOrders();
            List<FoodOrderTM> orderTMs = orderList.stream().map(dto -> new FoodOrderTM(
                    dto.getOrderId(),
                    dto.getReservationID(),
                    dto.getOrderType(),
                    dto.getStatus(),
                    dto.getTotalAmount(),
                    dto.getOrderDate()
            )).toList();

            ObservableList<FoodOrderTM> observableList = FXCollections.observableArrayList(orderTMs);
            tblFoodOrder.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to load food order data").show();
        }
    }


    public void btnCancelOnAction(ActionEvent actionEvent) {
        FoodOrderTM selectedOrder = tblFoodOrder.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a food order to cancel").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel order " + selectedOrder.getOrderId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean isDeleted = foodOrderModel.cancelFoodOrder(selectedOrder.getOrderId());
                if (isDeleted) {
                    tblFoodOrder.getItems().remove(selectedOrder);
                    new Alert(Alert.AlertType.INFORMATION, "✅ Food order cancelled successfully").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "❌ Failed to cancel food order").show();
                }
            }
        });
    }

    public void btnEditOnAction(ActionEvent actionEvent) {
        FoodOrderTM selectedOrder = tblFoodOrder.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please select a food order to edit").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FoodOrderView.fxml"));
            Parent load = loader.load();

            FoodOrderController controller = loader.getController();
            controller.setFoodOrderData(new FoodOrderDTO(
                    selectedOrder.getOrderId(),
                    selectedOrder.getReservationID(),
                    selectedOrder.getOrderType(),
                    selectedOrder.getStatus(),
                    selectedOrder.getTotalAmount(),
                    selectedOrder.getOrderDate()
            ));

            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Edit Food Order");
            stage.show();

            stage.setOnHiding(event -> loadFoodOrderData());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Edit Food Order window").show();
        }
    }

    public void btnAddNewFoodOrderOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FoodOrderView.fxml"));
            Parent load = loader.load();
            FoodOrderController controller = loader.getController();
            controller.setFoodOrderTableController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(load));
            stage.setTitle("Add New Food Order");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "❌ Failed to open Add Food window").show();
        }
    }
}
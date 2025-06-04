package lk.ijse.hotelmanagementsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.hotelmanagementsystem.dto.SupplierDTO;
import lk.ijse.hotelmanagementsystem.model.SupplierModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class SupplierController implements Initializable {
    public Label lblSupplierId;
    public TextField txtAddress;
    public TextField txtEmail;
    public TextField txtName;
    public TextField txtContact;
    public Button btnCancel;
    public Button btnSave;

    private SupplierTableController supplierTableController;
    private final SupplierModel supplierModel = new SupplierModel();
    private boolean isEditMode = false;
    private String existingSupplierId = null;
    private final String supplierIdValidation = "^SUP\\d{3}$";
    private final String nameValidation = "^[A-Za-z0-9\\s]+$";
    private final String emailValidation = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private final String contactValidation = "^[\\d\\-\\+\\s]+$";

    public void btnCancelOnAction(ActionEvent actionEvent) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel and reset the form?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            resetPage();
        }
    }

    private void resetPage() throws SQLException {
        txtName.clear();
        txtContact.clear();
        txtEmail.clear();
        txtAddress.clear();
        isEditMode = false;
        existingSupplierId = null;
        btnSave.setDisable(false);
        btnCancel.setDisable(false);
        lblSupplierId.setDisable(false);
        lblSupplierId.setText(SupplierModel.getNextSupplierId());
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws SQLException {
        String supplierId = lblSupplierId.getText();
        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || contact.isEmpty() || email.isEmpty() || address.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "⚠ Please fill in all required fields").show();
            return;
        }

        if (!supplierId.matches(supplierIdValidation)) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid Supplier ID format (e.g., SUP001)").show();
            return;
        }

        if (!name.matches(nameValidation)) {
            new Alert(Alert.AlertType.ERROR, "❌ Name can only contain letters, numbers, and spaces").show();
            return;
        }

        if (!email.matches(emailValidation)) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid email format").show();
            return;
        }

        if (!contact.matches(contactValidation)) {
            new Alert(Alert.AlertType.ERROR, "❌ Invalid contact format (digits, +, -, spaces allowed)").show();
            return;
        }

        SupplierDTO supplierDTO = new SupplierDTO(supplierId, name, contact, email, address);

        if (isEditMode) {
            boolean isUpdated = supplierModel.updateSupplier(supplierDTO);
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "✅ Supplier updated successfully").show();
                if (supplierTableController != null) {
                    supplierTableController.loadSupplierData();
                }
                Stage stage = (Stage) btnSave.getScene().getWindow();
                stage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "❌ Failed to update supplier").show();
            }
        } else {
            boolean isSaved = supplierModel.saveSupplier(supplierDTO);
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "✅ Supplier saved successfully").show();
                resetPage();
                if (supplierTableController != null) {
                    supplierTableController.loadSupplierData();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "❌ Failed to save supplier").show();
            }
        }
    }

    public void setSupplierTableController(SupplierTableController supplierTableController) {
        this.supplierTableController = supplierTableController;
    }

    public void setSupplierData(SupplierDTO supplierDTO) {
        if (supplierDTO == null) {
            new Alert(Alert.AlertType.ERROR, "❌ No supplier data provided").show();
            return;
        }

        isEditMode = true;
        existingSupplierId = supplierDTO.getSupplierId();
        lblSupplierId.setText(supplierDTO.getSupplierId());
        txtName.setText(supplierDTO.getName());
        txtContact.setText(supplierDTO.getContact());
        txtEmail.setText(supplierDTO.getEmail());
        txtAddress.setText(supplierDTO.getAddress());

        txtName.setDisable(false);
        txtContact.setDisable(false);
        txtEmail.setDisable(false);
        txtAddress.setDisable(false);
        btnSave.setDisable(false);
        btnCancel.setDisable(false);
        lblSupplierId.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            lblSupplierId.setText(SupplierModel.getNextSupplierId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

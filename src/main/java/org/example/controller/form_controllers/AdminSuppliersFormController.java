package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.entity.SupplierEntity;
import org.example.model.Supplier;
import org.example.service.ServiceFactory;
import org.example.service.custom.SupplierService;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminSuppliersFormController implements Initializable {

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXTextField addressTxt;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private JFXButton cashiersBtn;

    @FXML
    private JFXTextField contactTxt;

    @FXML
    private Label dateTxt;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private JFXTextField emailTxt;

    @FXML
    private JFXButton inventoryBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private JFXTextField nameTxt;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private Text orderIdTxt;

    @FXML
    private TableView<SupplierEntity> productTbl;

    @FXML
    private JFXButton reportsBtn;

    @FXML
    private TableColumn<SupplierEntity, String> supplierAddressCol;

    @FXML
    private TableColumn<SupplierEntity, String> supplierContactCol;

    @FXML
    private TableColumn<SupplierEntity, String> supplierEmailCol;

    @FXML
    private TableColumn<SupplierEntity, String> supplierIdCol;

    @FXML
    private JFXTextField supplierIdSearchTxt;

    @FXML
    private TableColumn<SupplierEntity, String> supplierNameCol;

    @FXML
    private JFXButton suppliersBtn;

    @FXML
    private JFXButton updateBtn;

    private final SupplierService supplierService = ServiceFactory.getInstance().getService(ServiceType.SUPPLIER);
    private ObservableList<SupplierEntity> masterList = FXCollections.observableArrayList();
    private FilteredList<SupplierEntity> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateTxt.setText(LocalDate.now().toString());

        supplierIdCol.setCellValueFactory(new PropertyValueFactory<>("supId"));
        supplierNameCol.setCellValueFactory(new PropertyValueFactory<>("supName"));
        supplierContactCol.setCellValueFactory(new PropertyValueFactory<>("supContact"));
        supplierEmailCol.setCellValueFactory(new PropertyValueFactory<>("supEmail"));
        supplierAddressCol.setCellValueFactory(new PropertyValueFactory<>("supAddress"));

        loadSuppliers();

        productTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFields(newVal);
            }
        });

        if (supplierIdSearchTxt != null) {
            supplierIdSearchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(s -> {
                    if (newValue == null || newValue.trim().isEmpty()) return true;
                    String q = newValue.trim().toLowerCase();
                    return (s.getSupId() != null && s.getSupId().toLowerCase().contains(q))
                            || (s.getSupName() != null && s.getSupName().toLowerCase().contains(q))
                            || (s.getSupContact() != null && s.getSupContact().contains(q));
                });
            });
        }
    }

    private void loadSuppliers() {
        masterList = supplierService.getAllSuppliers();
        filteredList = new FilteredList<>(masterList, p -> true);
        productTbl.setItems(filteredList);
    }

    private void populateFields(SupplierEntity supplier) {
        orderIdTxt.setText(supplier.getSupId());
        nameTxt.setText(supplier.getSupName() != null ? supplier.getSupName() : "");
        contactTxt.setText(supplier.getSupContact() != null ? supplier.getSupContact() : "");
        emailTxt.setText(supplier.getSupEmail() != null ? supplier.getSupEmail() : "");
        addressTxt.setText(supplier.getSupAddress() != null ? supplier.getSupAddress() : "");
    }

    @FXML
    void addButtonOnAction(ActionEvent event) {
        try {
            String name = nameTxt.getText();
            String contact = contactTxt.getText();
            String email = emailTxt.getText();
            String address = addressTxt.getText();

            if (name == null || name.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Supplier name is required.");
                return;
            }

            Supplier supplier = new Supplier();
            supplier.setSupName(name.trim());
            supplier.setSupContact(contact != null ? contact.trim() : "");
            supplier.setSupEmail(email != null ? email.trim() : "");
            supplier.setSupAddress(address != null ? address.trim() : "");

            boolean added = supplierService.addSupplier(supplier);
            if (added) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully!");
                cancelBtnOnAction(null);
                loadSuppliers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add supplier.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding supplier: " + e.getMessage());
        }
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        String id = orderIdTxt.getText();
        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a supplier to update.");
            return;
        }

        Supplier supplier = new Supplier();
        supplier.setSupId(id.trim());
        supplier.setSupName(nameTxt.getText().trim());
        supplier.setSupContact(contactTxt.getText().trim());
        supplier.setSupEmail(emailTxt.getText().trim());
        supplier.setSupAddress(addressTxt.getText().trim());

        boolean updated = supplierService.updateSupplier(supplier);
        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier updated successfully!");
            cancelBtnOnAction(null);
            loadSuppliers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update supplier.");
        }
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        String id = orderIdTxt.getText();
        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a supplier to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete supplier " + id + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            boolean deleted = supplierService.deleteSupplier(id.trim());
            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Supplier deleted successfully!");
                cancelBtnOnAction(null);
                loadSuppliers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete supplier. Check if products are linked.");
            }
        }
    }

    @FXML
    void cancelBtnOnAction(ActionEvent event) {
        orderIdTxt.setText("");
        nameTxt.clear();
        contactTxt.clear();
        emailTxt.clear();
        addressTxt.clear();
        if (supplierIdSearchTxt != null) supplierIdSearchTxt.clear();
        productTbl.getSelectionModel().clearSelection();
        loadSuppliers();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    void cashiersBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminEmployeeForm.fxml");
    }

    @FXML
    void inventoryBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminInventoryForm.fxml");
    }

    @FXML
    void logoutBtnOnAction(ActionEvent event) {
        navigate(event, "/view/loginForm.fxml");
    }

    @FXML
    void orderDetailsBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminOrderDetailsForm.fxml");
    }

    @FXML
    void reportsBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminReportsForm.fxml");
    }

    @FXML
    void suppliersBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminSupplierForm.fxml");
    }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxmlPath))));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load screen: " + fxmlPath);
        }
    }
}

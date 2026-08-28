package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.entity.ProductEntity;
import org.example.model.Product;
import org.example.service.ServiceFactory;
import org.example.service.custom.ProductService;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminInventoryFormController implements Initializable {

    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private JFXButton cashiersBtn;

    @FXML
    private JFXComboBox<String> categoryCmb;

    @FXML
    private JFXComboBox<String> categoryCmbBx;

    @FXML
    private TableColumn<ProductEntity, String> categoryCol;

    @FXML
    private Label dateTxt;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private JFXButton inventoryBtn1;

    @FXML
    private TableView<ProductEntity> inventoryTbl;

    @FXML
    private Button logoutBtn1;

    @FXML
    private JFXButton orderDetailsBtn1;

    @FXML
    private Text proIdTxt;

    @FXML
    private TableColumn<ProductEntity, String> productIdCol;

    @FXML
    private JFXTextField productIdTxt;

    @FXML
    private TableColumn<ProductEntity, String> productNameCol;

    @FXML
    private JFXTextField productNameTxt;

    @FXML
    private JFXTextField productSizeTxt;

    @FXML
    private TableColumn<ProductEntity, Integer> qtyCol;

    @FXML
    private JFXTextField qtyTxt;

    @FXML
    private JFXButton reportsBtn1;

    @FXML
    private JFXComboBox<String> sizeCmbBx;

    @FXML
    private TableColumn<ProductEntity, String> sizeCol;

    @FXML
    private TableColumn<ProductEntity, String> supIdCol;

    @FXML
    private JFXTextField supIdTxt;

    @FXML
    private JFXButton suppliersBtn;

    @FXML
    private TableColumn<ProductEntity, Double> unitPriceCol;

    @FXML
    private JFXTextField unitPriceTxt;

    @FXML
    private JFXButton updateBtn;

    private final ProductService productService = ServiceFactory.getInstance().getService(ServiceType.PRODUCT);
    private ObservableList<ProductEntity> masterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateTxt.setText(LocalDate.now().toString());

        categoryCmbBx.getItems().addAll("All", "Gents", "Ladies", "Kids");
        categoryCmbBx.setValue("All");

        sizeCmbBx.getItems().addAll("All", "XS", "S", "M", "L", "XL", "XXL", "XXXL");
        sizeCmbBx.setValue("All");

        categoryCmb.getItems().addAll("Gents", "Ladies", "Kids");

        productIdCol.setCellValueFactory(new PropertyValueFactory<>("proId"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("proName"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("proCategory"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("proSize"));
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("proPrice"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("stockQty"));

        supIdCol.setCellValueFactory(cellData -> {
            ProductEntity entity = cellData.getValue();
            String supId = (entity != null && entity.getSupplier() != null) ? entity.getSupplier().getSupId() : "";
            return new SimpleStringProperty(supId);
        });

        inventoryTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });

        categoryCmbBx.setOnAction(e -> applyFilter());
        sizeCmbBx.setOnAction(e -> applyFilter());
        productIdTxt.setOnAction(e -> searchProduct());

        loadTableData();
    }

    private void loadTableData() {
        masterList = productService.getAllProducts();
        inventoryTbl.setItems(masterList);
    }

    private void populateFields(ProductEntity entity) {
        proIdTxt.setText(entity.getProId());
        productNameTxt.setText(entity.getProName());
        categoryCmb.setValue(entity.getProCategory());
        productSizeTxt.setText(entity.getProSize());
        supIdTxt.setText(entity.getSupplier() != null ? entity.getSupplier().getSupId() : "");
        unitPriceTxt.setText(entity.getProPrice() != null ? String.valueOf(entity.getProPrice()) : "0.0");
        qtyTxt.setText(entity.getStockQty() != null ? String.valueOf(entity.getStockQty()) : "0");
    }

    private void applyFilter() {
        String cat = categoryCmbBx.getValue();
        String size = sizeCmbBx.getValue();

        ObservableList<ProductEntity> filtered = FXCollections.observableArrayList();
        for (ProductEntity p : masterList) {
            boolean matchCat = cat == null || cat.equals("All") || (p.getProCategory() != null && p.getProCategory().equalsIgnoreCase(cat));
            boolean matchSize = size == null || size.equals("All") || (p.getProSize() != null && p.getProSize().equalsIgnoreCase(size));
            if (matchCat && matchSize) {
                filtered.add(p);
            }
        }
        inventoryTbl.setItems(filtered);
    }

    private void searchProduct() {
        String query = productIdTxt.getText() != null ? productIdTxt.getText().trim() : "";
        if (query.isEmpty()) {
            inventoryTbl.setItems(masterList);
            return;
        }

        ObservableList<ProductEntity> results = FXCollections.observableArrayList();
        for (ProductEntity p : masterList) {
            if ((p.getProId() != null && p.getProId().equalsIgnoreCase(query))
                    || (p.getSku() != null && p.getSku().equalsIgnoreCase(query))
                    || (p.getProName() != null && p.getProName().toLowerCase().contains(query.toLowerCase()))) {
                results.add(p);
            }
        }
        inventoryTbl.setItems(results);
    }

    @FXML
    void addBtnOnAction(ActionEvent event) {
        try {
            String name = productNameTxt.getText();
            String cat = categoryCmb.getValue();
            String size = productSizeTxt.getText();
            String supId = supIdTxt.getText();
            double price = Double.parseDouble(unitPriceTxt.getText().trim());
            int qty = Integer.parseInt(qtyTxt.getText().trim());

            if (name == null || name.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Product name cannot be empty.");
                return;
            }

            Product product = new Product();
            product.setProName(name.trim());
            product.setProCategory(cat != null ? cat : "Gents");
            product.setProSize(size != null ? size.trim().toUpperCase() : "M");
            product.setSupId(supId != null ? supId.trim() : null);
            product.setProPrice(price);
            product.setCostPrice(price * 0.7); // default 30% margin if not specified
            product.setStockQty(qty);
            product.setReorderLevel(10);
            product.setSku("TEE-" + (cat != null ? cat.substring(0, 3).toUpperCase() : "GEN") + "-" + (size != null ? size.trim().toUpperCase() : "M") + "-" + System.currentTimeMillis() % 10000);

            Boolean added = productService.addProduct(product);
            if (Boolean.TRUE.equals(added)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
                cancelBtnOnAction(null);
                loadTableData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add product. Check Supplier ID if provided.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter valid numbers for Unit Price and Quantity.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding product: " + e.getMessage());
        }
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        try {
            String id = proIdTxt.getText();
            if (id == null || id.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a product from the table to update.");
                return;
            }

            Product product = productService.searchProductById(id);
            if (product == null) {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Product not found.");
                return;
            }

            product.setProName(productNameTxt.getText().trim());
            product.setProCategory(categoryCmb.getValue());
            product.setProSize(productSizeTxt.getText().trim().toUpperCase());
            product.setSupId(supIdTxt.getText().trim());
            product.setProPrice(Double.parseDouble(unitPriceTxt.getText().trim()));
            product.setStockQty(Integer.parseInt(qtyTxt.getText().trim()));

            boolean updated = productService.updateProduct(product);
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
                cancelBtnOnAction(null);
                loadTableData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update product.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter valid numbers for Unit Price and Quantity.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error updating product: " + e.getMessage());
        }
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        String id = proIdTxt.getText();
        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a product to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete product " + id + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Product deleted successfully!");
                cancelBtnOnAction(null);
                loadTableData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete product. It may be linked to orders.");
            }
        }
    }

    @FXML
    void cancelBtnOnAction(ActionEvent event) {
        proIdTxt.setText("");
        productNameTxt.clear();
        categoryCmb.setValue(null);
        productSizeTxt.clear();
        supIdTxt.clear();
        unitPriceTxt.clear();
        qtyTxt.clear();
        productIdTxt.clear();
        categoryCmbBx.setValue("All");
        sizeCmbBx.setValue("All");
        inventoryTbl.getSelectionModel().clearSelection();
        loadTableData();
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

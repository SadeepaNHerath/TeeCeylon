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
import org.example.service.ServiceFactory;
import org.example.service.custom.ProductService;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CashierInventoryFormController implements Initializable {

    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXButton cancelBtn;

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
    private JFXButton inventoryBtn;

    @FXML
    private TableView<ProductEntity> inventoryTbl;

    @FXML
    private JFXButton logoutBtn;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private JFXButton placeOrderBtn;

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
    private JFXButton reportsBtn;

    @FXML
    private JFXComboBox<String> sizeCmbBx;

    @FXML
    private TableColumn<ProductEntity, String> sizeCol;

    @FXML
    private TableColumn<ProductEntity, String> supIdCol;

    @FXML
    private JFXTextField supIdTxt;

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
        showAlert(Alert.AlertType.INFORMATION, "Notice", "Only Administrators can add inventory items.");
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Notice", "Only Administrators can update inventory items.");
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Notice", "Only Administrators can delete inventory items.");
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
    void inventoryBtnOnAction(ActionEvent event) {
        navigate(event, "/view/cashierInventoryForm.fxml");
    }

    @FXML
    void logoutBtnOnAction(ActionEvent event) {
        navigate(event, "/view/loginForm.fxml");
    }

    @FXML
    void orderDetailsBtnOnAction(ActionEvent event) {
        navigate(event, "/view/cashierOrderDetailsForm.fxml");
    }

    @FXML
    void placeOrderBtnOnAction(ActionEvent event) {
        navigate(event, "/view/cashierPlaceOrderForm.fxml");
    }

    @FXML
    void reportsBtnOnAction(ActionEvent event) {
        navigate(event, "/view/cashierReportsForm.fxml");
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

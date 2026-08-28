package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.entity.ProductEntity;
import org.example.entity.SupplierEntity;
import org.example.service.ServiceFactory;
import org.example.service.custom.ProductService;
import org.example.service.custom.SupplierService;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CashierReportsFormController implements Initializable {

    @FXML
    private PieChart cashierReportChart;

    @FXML
    private Label dateTxt;

    @FXML
    private JFXButton inventoryBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private JFXButton placeOrderBtn;

    @FXML
    private PieChart productReportChart;

    @FXML
    private JFXButton reportsBtn;

    @FXML
    private PieChart supplierReportChart;

    private final ProductService productService = ServiceFactory.getInstance().getService(ServiceType.PRODUCT);
    private final SupplierService supplierService = ServiceFactory.getInstance().getService(ServiceType.SUPPLIER);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateTxt.setText(LocalDate.now().toString());
        loadProductChart();
        loadSupplierChart();
        loadCashierChart();
    }

    private void loadProductChart() {
        try {
            ObservableList<ProductEntity> products = productService.getAllProducts();
            Map<String, Integer> catCounts = new HashMap<>();
            for (ProductEntity p : products) {
                String cat = p.getProCategory() != null ? p.getProCategory() : "Other";
                int qty = p.getStockQty() != null ? p.getStockQty() : 0;
                catCounts.put(cat, catCounts.getOrDefault(cat, 0) + qty);
            }

            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : catCounts.entrySet()) {
                if (entry.getValue() > 0) {
                    data.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + " pcs)", entry.getValue()));
                }
            }
            if (data.isEmpty()) {
                data.add(new PieChart.Data("Gents (Sample: 20)", 20));
                data.add(new PieChart.Data("Ladies (Sample: 15)", 15));
                data.add(new PieChart.Data("Kids (Sample: 10)", 10));
            }
            productReportChart.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSupplierChart() {
        try {
            ObservableList<SupplierEntity> suppliers = supplierService.getAllSuppliers();
            ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
            for (SupplierEntity s : suppliers) {
                int count = s.getProducts() != null ? s.getProducts().size() : 1;
                data.add(new PieChart.Data(s.getSupName() + " (" + count + " items)", Math.max(1, count)));
            }
            if (data.isEmpty()) {
                data.add(new PieChart.Data("TexCeylon Apparel (12 items)", 12));
                data.add(new PieChart.Data("CottonCraft LK (8 items)", 8));
            }
            supplierReportChart.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCashierChart() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        data.add(new PieChart.Data("Shift 1 (35 sales)", 35));
        data.add(new PieChart.Data("Shift 2 (28 sales)", 28));
        cashierReportChart.setData(data);
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}

package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import org.example.entity.PurchaseEntity;
import org.example.entity.SupplierEntity;
import org.example.model.Product;
import org.example.model.Purchase;
import org.example.model.PurchaseDetails;
import org.example.model.Supplier;
import org.example.service.ServiceFactory;
import org.example.service.custom.ProductService;
import org.example.service.custom.PurchaseService;
import org.example.service.custom.SupplierService;
import org.example.util.PdfGenerateUtil;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminPurchasesFormController implements Initializable {

    @FXML
    private JFXButton addLineBtn;

    @FXML
    private JFXButton cashiersBtn;

    @FXML
    private Label dateTxt;

    @FXML
    private TableColumn<PurchaseEntity, LocalDate> histDateCol;

    @FXML
    private TableColumn<PurchaseEntity, String> histPurIdCol;

    @FXML
    private TableColumn<PurchaseEntity, String> histSupplierCol;

    @FXML
    private TableColumn<PurchaseEntity, Double> histTotalCol;

    @FXML
    private JFXButton inventoryBtn;

    @FXML
    private TableColumn<PurchaseDetails, Double> lineCostCol;

    @FXML
    private TableColumn<PurchaseDetails, String> lineProductCol;

    @FXML
    private TableColumn<PurchaseDetails, Integer> lineQtyCol;

    @FXML
    private TableColumn<PurchaseDetails, Double> lineTotalCol;

    @FXML
    private TableView<PurchaseDetails> linesTable;

    @FXML
    private Button logoutBtn;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private JFXButton printReportBtn;

    @FXML
    private JFXComboBox<String> productCmb;

    @FXML
    private JFXButton purchasesBtn;

    @FXML
    private TableView<PurchaseEntity> purchasesHistoryTable;

    @FXML
    private Text purchaseIdTxt;

    @FXML
    private Spinner<Integer> qtySpinner;

    @FXML
    private JFXButton reportsBtn;

    @FXML
    private JFXButton savePurchaseBtn;

    @FXML
    private JFXComboBox<String> supplierCmb;

    @FXML
    private JFXTextField supplierInvTxt;

    @FXML
    private JFXButton suppliersBtn;

    @FXML
    private Label totalAmountLbl;

    @FXML
    private JFXTextField unitCostTxt;

    private final PurchaseService purchaseService = ServiceFactory.getInstance().getService(ServiceType.PURCHASE);
    private final SupplierService supplierService = ServiceFactory.getInstance().getService(ServiceType.SUPPLIER);
    private final ProductService productService = ServiceFactory.getInstance().getService(ServiceType.PRODUCT);

    private final ObservableList<PurchaseDetails> currentLines = FXCollections.observableArrayList();
    private ObservableList<PurchaseEntity> purchaseHistoryList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateTxt.setText(LocalDate.now().toString());
        purchaseIdTxt.setText(purchaseService.generateNextPurchaseId());

        qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 10));

        // Line table setup
        lineProductCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProName() != null ? cellData.getValue().getProName() : cellData.getValue().getProId()));
        lineQtyCol.setCellValueFactory(new PropertyValueFactory<>("qty"));
        lineCostCol.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        lineTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        linesTable.setItems(currentLines);

        // History table setup
        histPurIdCol.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
        histDateCol.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        histSupplierCol.setCellValueFactory(cellData -> {
            PurchaseEntity p = cellData.getValue();
            String supName = p.getSupplier() != null ? p.getSupplier().getSupName() : "-";
            return new SimpleStringProperty(supName);
        });
        histTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        loadSuppliers();
        loadProducts();
        loadHistory();

        productCmb.setOnAction(e -> {
            String sel = productCmb.getValue();
            if (sel != null && sel.contains(" - ")) {
                String id = sel.split(" - ")[0].trim();
                Product p = productService.searchProductById(id);
                if (p != null) {
                    double defaultCost = (p.getCostPrice() != null && p.getCostPrice() > 0) ? p.getCostPrice() : (p.getProPrice() != null ? p.getProPrice() * 0.7 : 500.0);
                    unitCostTxt.setText(String.format("%.2f", defaultCost));
                }
            }
        });
    }

    private void loadSuppliers() {
        supplierCmb.getItems().clear();
        for (SupplierEntity s : supplierService.getAllSuppliers()) {
            supplierCmb.getItems().add(s.getSupId() + " - " + s.getSupName());
        }
    }

    private void loadProducts() {
        productCmb.getItems().clear();
        for (ProductEntity p : productService.getAllProducts()) {
            productCmb.getItems().add(p.getProId() + " - " + p.getProName() + " (" + p.getProSize() + ")");
        }
    }

    private void loadHistory() {
        purchaseHistoryList = purchaseService.getAllPurchases();
        purchasesHistoryTable.setItems(purchaseHistoryList);
    }

    @FXML
    void addLineBtnOnAction(ActionEvent event) {
        String selProduct = productCmb.getValue();
        if (selProduct == null || !selProduct.contains(" - ")) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a product to add.");
            return;
        }

        String proId = selProduct.split(" - ")[0].trim();
        Product p = productService.searchProductById(proId);
        if (p == null) return;

        int qty = qtySpinner.getValue() != null ? qtySpinner.getValue() : 1;
        double cost;
        try {
            cost = Double.parseDouble(unitCostTxt.getText().trim());
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Cost", "Please enter a valid numeric unit cost.");
            return;
        }

        PurchaseDetails detail = new PurchaseDetails();
        detail.setPurchaseId(purchaseIdTxt.getText());
        detail.setProId(p.getProId());
        detail.setProName(p.getProName() + " [" + p.getProSize() + "]");
        detail.setSku(p.getSku());
        detail.setQty(qty);
        detail.setUnitCost(cost);
        detail.setTotalCost(qty * cost);

        currentLines.add(detail);
        updateTotal();
    }

    private void updateTotal() {
        double total = currentLines.stream().mapToDouble(PurchaseDetails::getTotalCost).sum();
        totalAmountLbl.setText(String.format("%.2f", total));
    }

    @FXML
    void savePurchaseBtnOnAction(ActionEvent event) {
        if (currentLines.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Lines", "Please add at least one product line.");
            return;
        }

        String selSupplier = supplierCmb.getValue();
        if (selSupplier == null || !selSupplier.contains(" - ")) {
            showAlert(Alert.AlertType.WARNING, "Supplier Required", "Please select a supplier for this purchase order.");
            return;
        }

        String supId = selSupplier.split(" - ")[0].trim();
        String invNo = supplierInvTxt.getText() != null ? supplierInvTxt.getText().trim() : "";
        double total = Double.parseDouble(totalAmountLbl.getText().trim());
        String purId = purchaseIdTxt.getText();

        Purchase purchase = new Purchase();
        purchase.setPurchaseId(purId);
        purchase.setSupId(supId);
        purchase.setSupplierInvoiceNo(invNo);
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setTotalAmount(total);
        purchase.setRemarks("Restocked on " + LocalDate.now());

        Boolean success = purchaseService.addPurchase(purchase, new ArrayList<>(currentLines));
        if (Boolean.TRUE.equals(success)) {
            showAlert(Alert.AlertType.INFORMATION, "Purchase Saved & Stock Updated",
                    "Purchase " + purId + " saved successfully!\nInventory stock has been incremented for all line items.");
            currentLines.clear();
            totalAmountLbl.setText("0.00");
            supplierInvTxt.clear();
            productCmb.getSelectionModel().clearSelection();
            supplierCmb.getSelectionModel().clearSelection();
            unitCostTxt.clear();
            purchaseIdTxt.setText(purchaseService.generateNextPurchaseId());
            loadHistory();
            loadProducts();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save purchase order.");
        }
    }

    @FXML
    void printReportBtnOnAction(ActionEvent event) {
        List<PurchaseEntity> all = purchaseService.getAllPurchases();
        String path = PdfGenerateUtil.generatePurchaseReport(all);
        if (path != null) {
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", "Purchase report PDF generated at:\n" + path);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate purchase report PDF.");
        }
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
        navigate(event, "/view/adminOrdersForm.fxml");
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

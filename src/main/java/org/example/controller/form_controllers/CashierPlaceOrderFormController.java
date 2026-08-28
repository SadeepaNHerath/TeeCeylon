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
import org.example.entity.CustomerEntity;
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.entity.ProductEntity;
import org.example.model.Customer;
import org.example.model.Order;
import org.example.model.OrderDetails;
import org.example.model.Product;
import org.example.service.ServiceFactory;
import org.example.service.custom.CustomerService;
import org.example.service.custom.OrderService;
import org.example.service.custom.ProductService;
import org.example.util.PdfGenerateUtil;
import org.example.util.ServiceType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CashierPlaceOrderFormController implements Initializable {

    @FXML
    private JFXButton addToCartBtn;

    @FXML
    private TableView<OrderDetailsEntity> cartTbl;

    @FXML
    private JFXTextField cusEmailTxt;

    @FXML
    private JFXTextField cusNameTxt;

    @FXML
    private Label dateTxt;

    @FXML
    private JFXButton inventoryBtn;

    @FXML
    private JFXButton logoutBtn;

    @FXML
    private JFXButton orderBtn;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private Text orderIdTxt;

    @FXML
    private JFXButton placeOrderBtn;

    @FXML
    private JFXComboBox<String> productIdCmbBx;

    @FXML
    private TableColumn<OrderDetailsEntity, String> productIdCol;

    @FXML
    private JFXTextField productNameTxt;

    @FXML
    private TableColumn<OrderDetailsEntity, Integer> qtyCol;

    @FXML
    private Spinner<Integer> quantitySpnr;

    @FXML
    private JFXButton reportsBtn;

    @FXML
    private TableColumn<OrderDetailsEntity, Double> totalCol;

    @FXML
    private Label totalTxt;

    @FXML
    private Label txtSize;

    @FXML
    private Label txtUnitPrice;

    @FXML
    private TableColumn<OrderDetailsEntity, Double> unitPriceCol;

    private final ObservableList<OrderDetailsEntity> cartItems = FXCollections.observableArrayList();
    private final OrderService orderService = ServiceFactory.getInstance().getService(ServiceType.ORDER);
    private final ProductService productService = ServiceFactory.getInstance().getService(ServiceType.PRODUCT);
    private final CustomerService customerService = ServiceFactory.getInstance().getService(ServiceType.CUSTOMER);

    private List<ProductEntity> availableProducts = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateTxt.setText(LocalDate.now().toString());
        orderIdTxt.setText(orderService.generateNextOrderId());

        productIdCol.setCellValueFactory(cellData -> {
            OrderDetailsEntity entity = cellData.getValue();
            String display = entity.getProId();
            if (entity.getProduct() != null && entity.getProduct().getSku() != null) {
                display = entity.getProduct().getSku();
            }
            return new SimpleStringProperty(display);
        });

        qtyCol.setCellValueFactory(new PropertyValueFactory<>("proQty"));
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("proTotal"));

        cartTbl.setItems(cartItems);

        quantitySpnr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        loadProducts();

        productIdCmbBx.setOnAction(event -> onProductSelected());

        // Right-click / double-click context menu to remove cart item
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Remove from Cart");
        removeItem.setOnAction(e -> {
            OrderDetailsEntity selected = cartTbl.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cartItems.remove(selected);
                updateTotal();
            }
        });
        contextMenu.getItems().add(removeItem);
        cartTbl.setContextMenu(contextMenu);
    }

    private void loadProducts() {
        productIdCmbBx.getItems().clear();
        availableProducts = productService.getAllProducts();
        for (ProductEntity product : availableProducts) {
            String label = product.getProId();
            productIdCmbBx.getItems().add(label);
        }
    }

    private void onProductSelected() {
        String selectedId = productIdCmbBx.getValue();
        if (selectedId != null && !selectedId.isEmpty()) {
            Product product = productService.searchProductById(selectedId);
            if (product != null) {
                productNameTxt.setText(product.getProName() != null ? product.getProName() : "");
                txtUnitPrice.setText(String.format("%.2f", product.getProPrice() != null ? product.getProPrice() : 0.0));
                txtSize.setText(product.getProSize() != null ? product.getProSize() : "-");

                int stock = product.getStockQty() != null ? product.getStockQty() : 0;
                int maxQty = Math.max(1, stock);
                quantitySpnr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxQty, 1));
            }
        }
    }

    @FXML
    private void addToCartBtnOnAction(ActionEvent actionEvent) {
        String selectedProductId = productIdCmbBx.getValue();
        if (selectedProductId == null || selectedProductId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a product to add to cart.");
            return;
        }

        Product product = productService.searchProductById(selectedProductId);
        if (product == null) {
            showAlert(Alert.AlertType.ERROR, "Not Found", "Selected product not found.");
            return;
        }

        int requestedQty = quantitySpnr.getValue() != null ? quantitySpnr.getValue() : 1;
        int currentStock = product.getStockQty() != null ? product.getStockQty() : 0;

        // Check stock already in cart
        int alreadyInCart = 0;
        OrderDetailsEntity existingLine = null;
        for (OrderDetailsEntity item : cartItems) {
            if (item.getProId().equals(product.getProId())) {
                alreadyInCart = item.getProQty();
                existingLine = item;
                break;
            }
        }

        if (alreadyInCart + requestedQty > currentStock) {
            showAlert(Alert.AlertType.WARNING, "Insufficient Stock",
                    "Cannot add " + requestedQty + " units. Available stock: " + currentStock + " (Already in cart: " + alreadyInCart + ")");
            return;
        }

        double unitPrice = product.getProPrice() != null ? product.getProPrice() : 0.0;

        if (existingLine != null) {
            existingLine.setProQty(alreadyInCart + requestedQty);
            existingLine.setProTotal(existingLine.getProQty() * unitPrice);
            cartTbl.refresh();
        } else {
            ProductEntity entity = new ProductEntity();
            entity.setProId(product.getProId());
            entity.setSku(product.getSku());
            entity.setProName(product.getProName());
            entity.setProSize(product.getProSize());
            entity.setProColor(product.getProColor());
            entity.setProPrice(unitPrice);

            OrderDetailsEntity line = new OrderDetailsEntity();
            line.setOrdId(orderIdTxt.getText());
            line.setProId(product.getProId());
            line.setProduct(entity);
            line.setProQty(requestedQty);
            line.setUnitPrice(unitPrice);
            line.setProTotal(requestedQty * unitPrice);

            cartItems.add(line);
        }

        updateTotal();
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(OrderDetailsEntity::getProTotal).sum();
        totalTxt.setText(String.format("%.2f", total));
    }

    @FXML
    private void OrderBtnOnAction(ActionEvent actionEvent) {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Cannot place an order with an empty cart.");
            return;
        }

        String customerName = cusNameTxt.getText() != null && !cusNameTxt.getText().trim().isEmpty() ? cusNameTxt.getText().trim() : "Walk-in Customer";
        String customerEmail = cusEmailTxt.getText() != null ? cusEmailTxt.getText().trim() : "";
        LocalDate orderDate = LocalDate.now();
        LocalTime orderTime = LocalTime.now();
        double orderTotal = Double.parseDouble(totalTxt.getText().trim());
        String currentOrdId = orderIdTxt.getText();

        Order order = new Order();
        order.setOrdId(currentOrdId);
        order.setCusName(customerName);
        order.setCusEmail(customerEmail);
        order.setOrdDate(orderDate);
        order.setOrdTime(orderTime);
        order.setOrdTotal(orderTotal);

        ObservableList<OrderDetails> orderDetailsList = FXCollections.observableArrayList();
        for (OrderDetailsEntity entity : cartItems) {
            OrderDetails details = new OrderDetails();
            details.setOrdId(currentOrdId);
            details.setProId(entity.getProId());
            details.setProQty(entity.getProQty());
            details.setUnitPrice(entity.getUnitPrice());
            details.setProTotal(entity.getProTotal());
            orderDetailsList.add(details);
        }

        Boolean success = orderService.addOrder(order, orderDetailsList);
        if (Boolean.TRUE.equals(success)) {
            // Build OrderEntity for PDF receipt
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrdId(currentOrdId);
            orderEntity.setCusName(customerName);
            orderEntity.setCusEmail(customerEmail);
            orderEntity.setOrdDate(orderDate);
            orderEntity.setOrdTime(orderTime);
            orderEntity.setOrdTotal(orderTotal);

            List<OrderDetailsEntity> detailsForPdf = new ArrayList<>(cartItems);
            String invoicePath = PdfGenerateUtil.generateOrderInvoice(orderEntity, detailsForPdf);

            String message = "Order " + currentOrdId + " placed successfully!\nTotal: Rs. " + String.format("%.2f", orderTotal);
            if (invoicePath != null) {
                message += "\nInvoice generated at: " + invoicePath;
            }

            showAlert(Alert.AlertType.INFORMATION, "Order Placed", message);

            clearForm();
            orderIdTxt.setText(orderService.generateNextOrderId());
            loadProducts();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to place order. Please verify item stock levels.");
        }
    }

    private void clearForm() {
        cusNameTxt.clear();
        cusEmailTxt.clear();
        productIdCmbBx.getSelectionModel().clearSelection();
        productNameTxt.clear();
        txtUnitPrice.setText("0.00");
        txtSize.setText("-");
        quantitySpnr.getValueFactory().setValue(1);
        cartItems.clear();
        totalTxt.setText("0.00");
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

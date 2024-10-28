package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
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
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.entity.ProductEntity;
import org.example.model.Order;
import org.example.model.OrderDetails;
import org.example.model.Product;
import org.example.service.ServiceFactory;
import org.example.service.custom.OrderService;
import org.example.service.custom.ProductService;
import org.example.util.ServiceType;

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

    private ObservableList<OrderDetailsEntity> cartItems = FXCollections.observableArrayList();
    private OrderService orderService = ServiceFactory.getInstance().getService(ServiceType.ORDER);
    private ProductService productService = ServiceFactory.getInstance().getService(ServiceType.PRODUCT);

    @FXML
    private void OrderBtnOnAction(ActionEvent actionEvent) {
        String customerName = cusNameTxt.getText();
        String customerEmail = cusEmailTxt.getText();
        LocalDate orderDate = LocalDate.now();
        LocalTime orderTime = LocalTime.now();
        double orderTotal = Double.parseDouble(totalTxt.getText());

        Order order = new Order();
        order.setOrdId(orderIdTxt.getText());
        order.setCusName(customerName);
        order.setCusEmail(customerEmail);
        order.setOrdDate(orderDate);
        order.setOrdTime(orderTime);
        order.setOrdTotal(orderTotal);

        ObservableList<OrderDetails> orderDetailsList = FXCollections.observableArrayList();
        for (OrderDetailsEntity entity : cartItems) {
            OrderDetails details = new OrderDetails();
            details.setOrdId(entity.getOrdId());
            details.setProId(entity.getProId());
            details.setProQty(entity.getProQty());
            details.setProTotal(entity.getProTotal());
            orderDetailsList.add(details);
        }

        orderService.addOrder(order, orderDetailsList);
        clearForm();
    }

    @FXML
    void inventoryBtnOnAction(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierInventoryForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void logoutBtnOnAction(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void orderDetailsBtnOnAction(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierOrderDetailsForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void placeOrderBtnOnAction(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierPlaceOrderForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void reportsBtnOnAction(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierReportsForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       qtyCol.setCellValueFactory(new PropertyValueFactory<>("proQty"));
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("proTotal"));
       totalCol.setCellValueFactory(new PropertyValueFactory<>("proTotal"));
       //loadProducts();
       quantitySpnr.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }

    private void loadProducts() {
        List<ProductEntity> products = productService.getAllProducts();
        for (ProductEntity product : products) {
            productIdCmbBx.getItems().add(product.getProId());
        }
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(OrderDetailsEntity::getProTotal).sum();
        totalTxt.setText(String.format("%.2f", total));
    }

    private void clearForm() {
        cusNameTxt.clear();
        cusEmailTxt.clear();
        productIdCmbBx.getSelectionModel().clearSelection();
        productNameTxt.clear();
        txtUnitPrice.setText("0");
        txtSize.setText("0");
        quantitySpnr.getValueFactory().setValue(0);
        cartItems.clear();
        cartTbl.setItems(cartItems);
        totalTxt.setText("0");

    }

    @FXML
    private void addToCartBtnOnAction(ActionEvent actionEvent) {
        String selectedProductId = productIdCmbBx.getValue();
        if (selectedProductId != null) {
            Product product = productService.searchProductById(selectedProductId);
            int quantity = quantitySpnr.getValue();
            double total = product.getProPrice() * quantity;

            OrderDetailsEntity orderDetails = new OrderDetailsEntity();
            orderDetails.setOrdId(orderIdTxt.getText());
            orderDetails.setProId(product.getProId());
            orderDetails.setProQty(quantity);
            orderDetails.setProTotal(total);

            cartItems.add(orderDetails);
            cartTbl.setItems(cartItems);
            updateTotal();
        }
    }

}
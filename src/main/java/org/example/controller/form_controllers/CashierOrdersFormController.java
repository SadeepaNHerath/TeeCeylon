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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.entity.OrderEntity;
import org.example.model.Order;
import org.example.model.OrderDetails;
import org.example.service.ServiceFactory;
import org.example.service.custom.OrderService;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CashierOrdersFormController implements Initializable {

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private TableColumn<OrderEntity, String> customerNameCol;

    @FXML
    private JFXTextField customerNameTxt;

    @FXML
    private TableColumn<OrderEntity, LocalDate> dateCol;

    @FXML
    private DatePicker dateSearchPckr;

    @FXML
    private Label dateTxt;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private TableColumn<OrderEntity, String> emailCol;

    @FXML
    private JFXTextField emailTxt;

    @FXML
    private JFXButton inventoryBtn;

    @FXML
    private JFXButton logoutBtn;

    @FXML
    private JFXTextField orderDateTxt;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private TableColumn<OrderEntity, String> orderIdCol;

    @FXML
    private JFXTextField orderIdSearchTxt;

    @FXML
    private Text orderIdTxt;

    @FXML
    private JFXButton placeOrderBtn;

    @FXML
    private TableView<OrderEntity> productTbl;

    @FXML
    private JFXButton reportsBtn;

    @FXML
    private TableColumn<OrderEntity, LocalTime> timeCol;

    @FXML
    private JFXTextField timeTxt;

    @FXML
    private TableColumn<OrderEntity, Double> totalCol;

    @FXML
    private JFXTextField totalTxt;

    @FXML
    private JFXButton updateBtn;

    private ObservableList<OrderEntity> orderList = FXCollections.observableArrayList();
    private FilteredList<OrderEntity> filteredList;
    private OrderService orderService ;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public CashierOrdersFormController() {
        this.orderService = ServiceFactory.getInstance().getService(ServiceType.ORDER);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateTxt.setText(LocalDate.now().toString());
        setupTableColumns();
        loadOrders();
        setupSearch();
        setupTableSelectionListener();
    }

    private void setupTableColumns() {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("ordId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("cusName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("cusEmail"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("ordDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("ordTime"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("ordTotal"));
    }

    private void setupSearch() {
        filteredList = new FilteredList<>(orderList, p -> true);

        // Search by Order ID
        orderIdSearchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(order -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return order.getOrdId().toLowerCase().contains(newValue.toLowerCase());
            });
            updateTableView();
        });

        // Search by Date
        dateSearchPckr.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(order -> {
                if (newValue == null) {
                    return true;
                }
                return order.getOrdDate().equals(newValue);
            });
            updateTableView();
        });
    }

    private void setupTableSelectionListener() {
        productTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void loadOrders() {
        try {
            ObservableList<OrderEntity> orders = orderService.getAllOrders();
            orderList.clear();
            orderList.addAll(orders);
            updateTableView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTableView() {
        productTbl.setItems(filteredList);
    }

    private void populateFields(OrderEntity order) {
        orderIdTxt.setText(order.getOrdId());
        customerNameTxt.setText(order.getCusName());
        emailTxt.setText(order.getCusEmail());
        orderDateTxt.setText(order.getOrdDate().toString());
        timeTxt.setText(order.getOrdTime().format(TIME_FORMATTER));
        totalTxt.setText(String.valueOf(order.getOrdTotal()));
    }

    private void clearFields() {
        orderIdTxt.setText("");
        customerNameTxt.clear();
        emailTxt.clear();
        orderDateTxt.clear();
        timeTxt.clear();
        totalTxt.clear();
    }

    @FXML
    void addButtonOnAction(ActionEvent event) {
        try {
            validateInputs();

            Order newOrder = new Order();
            newOrder.setCusName(customerNameTxt.getText());
            newOrder.setCusEmail(emailTxt.getText());
            newOrder.setOrdDate(LocalDate.parse(orderDateTxt.getText()));
            newOrder.setOrdTime(LocalTime.parse(timeTxt.getText(), TIME_FORMATTER));
            newOrder.setOrdTotal(Double.parseDouble(totalTxt.getText()));

            ObservableList<OrderDetails> orderDetails = FXCollections.observableArrayList(); // Assuming you have order details to add
            orderService.addOrder(newOrder, orderDetails);
            loadOrders();
            clearFields();

        } catch (Exception e) {

        }
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        try {
            OrderEntity selectedOrder = productTbl.getSelectionModel().getSelectedItem();
            if (selectedOrder == null) {
                return;
            }

            validateInputs();

            Order updatedOrder = new Order();
            updatedOrder.setCusName(customerNameTxt.getText());
            updatedOrder.setCusEmail(emailTxt.getText());
            updatedOrder.setOrdDate(LocalDate.parse(orderDateTxt.getText()));
            updatedOrder.setOrdTime(LocalTime.parse(timeTxt.getText(), TIME_FORMATTER));
            updatedOrder.setOrdTotal(Double.parseDouble(totalTxt.getText()));

            ObservableList<OrderDetails> orderDetails = FXCollections.observableArrayList();
            orderService.updateOrder(updatedOrder, orderDetails);
            loadOrders();
            clearFields();
        } catch (Exception e) {

        }
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        try {
            OrderEntity selectedOrder = productTbl.getSelectionModel().getSelectedItem();
            if (selectedOrder == null) {

                return;
            }

            orderService.deleteOrder(selectedOrder.getOrdId());
            loadOrders();
            clearFields();

        } catch (Exception e) {

        }
    }

    @FXML
    void cancelBtnOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void inventoryBtnOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierInventoryForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void logoutBtnOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void orderDetailsBtnOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierOrderDetailsForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void placeOrderBtnOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierPlaceOrderForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void reportsBtnOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierReportsForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateInputs() throws Exception {
        if (customerNameTxt.getText().isEmpty() || emailTxt.getText().isEmpty() || orderDateTxt.getText().isEmpty() || timeTxt.getText().isEmpty() || totalTxt.getText().isEmpty()) {
            throw new Exception("Please fill in all fields");
        }
    }
}
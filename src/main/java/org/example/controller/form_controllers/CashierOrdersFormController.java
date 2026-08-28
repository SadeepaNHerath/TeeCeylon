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
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.service.ServiceFactory;
import org.example.service.custom.OrderService;
import org.example.util.PdfGenerateUtil;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    private final OrderService orderService = ServiceFactory.getInstance().getService(ServiceType.ORDER);
    private ObservableList<OrderEntity> orderList = FXCollections.observableArrayList();
    private FilteredList<OrderEntity> filteredList;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateTxt.setText(LocalDate.now().toString());

        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("ordId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("cusName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("cusEmail"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("ordDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("ordTime"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("ordTotal"));

        loadOrders();
        setupSearch();

        productTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFields(newVal);
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem reprintItem = new MenuItem("Print / Reprint Invoice PDF");
        reprintItem.setOnAction(e -> reprintSelectedInvoice());
        contextMenu.getItems().add(reprintItem);
        productTbl.setContextMenu(contextMenu);
    }

    private void loadOrders() {
        orderList = orderService.getAllOrders();
        filteredList = new FilteredList<>(orderList, p -> true);
        productTbl.setItems(filteredList);
    }

    private void setupSearch() {
        orderIdSearchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(order -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;
                String q = newValue.trim().toLowerCase();
                return (order.getOrdId() != null && order.getOrdId().toLowerCase().contains(q))
                        || (order.getCusName() != null && order.getCusName().toLowerCase().contains(q))
                        || (order.getCusPhone() != null && order.getCusPhone().toLowerCase().contains(q));
            });
        });

        dateSearchPckr.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(order -> {
                if (newValue == null) return true;
                return order.getOrdDate() != null && order.getOrdDate().equals(newValue);
            });
        });
    }

    private void populateFields(OrderEntity order) {
        orderIdTxt.setText(order.getOrdId());
        customerNameTxt.setText(order.getCusName() != null ? order.getCusName() : "");
        emailTxt.setText(order.getCusEmail() != null ? order.getCusEmail() : "");
        orderDateTxt.setText(order.getOrdDate() != null ? order.getOrdDate().toString() : "");
        timeTxt.setText(order.getOrdTime() != null ? order.getOrdTime().format(TIME_FORMATTER) : "");
        totalTxt.setText(order.getOrdTotal() != null ? String.format("%.2f", order.getOrdTotal()) : "0.00");
    }

    private void reprintSelectedInvoice() {
        OrderEntity selected = productTbl.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an order from the table to print its invoice.");
            return;
        }

        OrderEntity fullOrder = orderService.searchOrderById(selected.getOrdId());
        if (fullOrder != null) {
            List<OrderDetailsEntity> details = fullOrder.getOrderDetails() != null ? fullOrder.getOrderDetails() : new ArrayList<>();
            String path = PdfGenerateUtil.generateOrderInvoice(fullOrder, details);
            if (path != null) {
                showAlert(Alert.AlertType.INFORMATION, "Invoice Generated", "Invoice PDF saved to:\n" + path);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate invoice PDF.");
            }
        }
    }

    @FXML
    void addButtonOnAction(ActionEvent event) {
        reprintSelectedInvoice();
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        reprintSelectedInvoice();
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Permission", "Please contact Administrator to cancel or delete orders.");
    }

    @FXML
    void cancelBtnOnAction(ActionEvent event) {
        orderIdTxt.setText("");
        customerNameTxt.clear();
        emailTxt.clear();
        orderDateTxt.clear();
        timeTxt.clear();
        totalTxt.clear();
        orderIdSearchTxt.clear();
        dateSearchPckr.setValue(null);
        productTbl.getSelectionModel().clearSelection();
        loadOrders();
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

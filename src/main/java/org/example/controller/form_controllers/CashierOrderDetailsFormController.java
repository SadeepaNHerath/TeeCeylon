package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.service.ServiceFactory;
import org.example.service.custom.OrderService;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CashierOrderDetailsFormController implements Initializable {

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private Label dateTxt;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private JFXButton inventoryBtn;

    @FXML
    private JFXButton logoutBtn;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private TableColumn<OrderDetailsEntity, String> orderIdCol;

    @FXML
    private Text orderIdTxt;

    @FXML
    private JFXButton placeOrderBtn;

    @FXML
    private TableColumn<OrderDetailsEntity, String> productIdCol;

    @FXML
    private JFXTextField productIdSearchTxt;

    @FXML
    private JFXTextField productIdTxt;

    @FXML
    private TableView<OrderDetailsEntity> productTbl;

    @FXML
    private TableColumn<OrderDetailsEntity, Integer> qtyCol;

    @FXML
    private JFXTextField qtyTxt;

    @FXML
    private JFXButton reportsBtn;

    @FXML
    private TableColumn<OrderDetailsEntity, Double> totCol;

    @FXML
    private JFXTextField totalTxt;

    @FXML
    private JFXButton updateBtn;

    private final OrderService orderService = ServiceFactory.getInstance().getService(ServiceType.ORDER);
    private ObservableList<OrderDetailsEntity> masterList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateTxt.setText(LocalDate.now().toString());

        orderIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrdId()));
        productIdCol.setCellValueFactory(cellData -> {
            OrderDetailsEntity entity = cellData.getValue();
            String display = entity.getProId();
            if (entity.getProduct() != null) {
                display = (entity.getProduct().getSku() != null ? entity.getProduct().getSku() : entity.getProId())
                        + " (" + (entity.getProduct().getProName() != null ? entity.getProduct().getProName() : "") + ")";
            }
            return new SimpleStringProperty(display);
        });
        qtyCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProQty() != null ? cellData.getValue().getProQty() : 0).asObject());
        totCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProTotal() != null ? cellData.getValue().getProTotal() : 0.0).asObject());

        productTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFields(newVal);
            }
        });

        loadOrderDetails();
    }

    private void loadOrderDetails() {
        masterList.clear();
        List<OrderEntity> orders = orderService.getAllOrders();
        for (OrderEntity o : orders) {
            if (o.getOrderDetails() != null) {
                masterList.addAll(o.getOrderDetails());
            }
        }
        productTbl.setItems(masterList);
    }

    private void populateFields(OrderDetailsEntity detail) {
        orderIdTxt.setText(detail.getOrdId());
        productIdTxt.setText(detail.getProId());
        qtyTxt.setText(detail.getProQty() != null ? String.valueOf(detail.getProQty()) : "0");
        totalTxt.setText(detail.getProTotal() != null ? String.format("%.2f", detail.getProTotal()) : "0.00");
    }

    @FXML
    void addButtonOnAction(ActionEvent event) {
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
    }

    @FXML
    void cancelBtnOnAction(ActionEvent event) {
        orderIdTxt.setText("");
        productIdTxt.clear();
        qtyTxt.clear();
        totalTxt.clear();
        productTbl.getSelectionModel().clearSelection();
        loadOrderDetails();
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

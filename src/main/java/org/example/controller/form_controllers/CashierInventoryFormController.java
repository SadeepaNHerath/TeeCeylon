package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class CashierInventoryFormController {

    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private JFXComboBox<?> categoryCmb;

    @FXML
    private JFXComboBox<?> categoryCmbBx;

    @FXML
    private TableColumn<?, ?> categoryCol;

    @FXML
    private Label dateTxt;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private JFXButton inventoryBtn;

    @FXML
    private TableView<?> inventoryTbl;

    @FXML
    private JFXButton logoutBtn;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private JFXButton placeOrderBtn;

    @FXML
    private Text proIdTxt;

    @FXML
    private TableColumn<?, ?> productIdCol;

    @FXML
    private JFXTextField productIdTxt;

    @FXML
    private TableColumn<?, ?> productNameCol;

    @FXML
    private JFXTextField productNameTxt;

    @FXML
    private JFXTextField productSizeTxt;

    @FXML
    private TableColumn<?, ?> qtyCol;

    @FXML
    private JFXTextField qtyTxt;

    @FXML
    private JFXButton reportsBtn;

    @FXML
    private JFXComboBox<?> sizeCmbBx;

    @FXML
    private TableColumn<?, ?> sizeCol;

    @FXML
    private TableColumn<?, ?> supIdCol;

    @FXML
    private JFXTextField supIdTxt;

    @FXML
    private TableColumn<?, ?> unitPriceCol;

    @FXML
    private JFXTextField unitPriceTxt;

    @FXML
    private JFXButton updateBtn;

    @FXML
    void addBtnOnAction(ActionEvent event) {

    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {

    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {

    }
    @FXML
    void cancelBtnOnAction(ActionEvent event) {

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

}

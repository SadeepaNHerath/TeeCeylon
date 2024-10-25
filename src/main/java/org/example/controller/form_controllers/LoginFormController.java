package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable {

    @FXML
    public JFXComboBox<String> roleCmbBx;
    @FXML
    private JFXTextField emailFld;

    @FXML
    private Text forgotPasswordTxt;

    @FXML
    private JFXButton loginBtn;

    @FXML
    private JFXPasswordField passwordFld;

    @FXML
    private Text signUptxt;

    @FXML
    void forgotPasswordOnClick(MouseEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/forgetPasswordForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void loginBtnOnaction(ActionEvent event) {
        if (roleCmbBx.getValue().equals("Admin")) {
            String email = emailFld.getText();
            String password = passwordFld.getText();
            if (email.equals("admin@gmail.com") && password.equals("admin")) {
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                try {
                    stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/adminReportsForm.fxml"))));
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else if (roleCmbBx.getValue().equals("Cashier")) {
            String email = emailFld.getText();
            String password = passwordFld.getText();
            if (email.equals("cashier@gmail.com") && password.equals("cashier")) {
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                try {
                    stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/cashierPlaceOrderForm.fxml"))));
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                System.out.println("Invalid email or password");
            }
        }
    }

    @FXML
    void registerOnClick(MouseEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/registerForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleCmbBx.getItems().addAll("Admin", "Cashier");
    }
}

package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterFormController {

    @FXML
    private JFXTextField addressFld;

    @FXML
    private Text alreadyRegisteredTxt;

    @FXML
    private JFXPasswordField confirmPasswordFld;

    @FXML
    private JFXTextField contactNumFld;

    @FXML
    private JFXTextField emailFld;

    @FXML
    private Label empIdLbl;

    @FXML
    private JFXTextField empNameFld;

    @FXML
    private JFXPasswordField passwordFld;

    @FXML
    private JFXButton registerBtn;

    @FXML
    void SignInOnClick(MouseEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void registerBtnOnAction(ActionEvent event) {

    }

}

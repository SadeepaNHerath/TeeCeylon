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
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.model.Employee;
import org.example.service.ServiceFactory;
import org.example.service.custom.EmployeeService;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(LoginFormController.class.getName());
    private final EmployeeService employeeService = ServiceFactory.getInstance().getService(ServiceType.EMPLOYEE);

    @FXML
    void forgotPasswordOnClick(MouseEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/forgetPasswordForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open forgot password form: " + e.getMessage());
        }
    }

    @FXML
    void loginBtnOnaction(ActionEvent event) {
        try {
            String role = roleCmbBx.getValue();
            String email = emailFld.getText();
            String password = passwordFld.getText();

            if (role == null || role.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Login Required", "Please select a role (Admin or Cashier).");
                return;
            }

            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Login Required", "Please enter both email and password.");
                return;
            }

            Employee authEmp = employeeService.authenticate(email.trim(), password.trim(), role);

            if (authEmp != null) {
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                String targetFxml = "Admin".equalsIgnoreCase(role) ? "/view/adminReportsForm.fxml" : "/view/cashierPlaceOrderForm.fxml";
                URL resource = getClass().getResource(targetFxml);
                if (resource != null) {
                    stage.setScene(new Scene(FXMLLoader.load(resource)));
                    stage.show();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Resource not found: " + targetFxml);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Authentication Failed", "Invalid email, password, or role selection.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Login error: " + e.getMessage());
        }
    }

    @FXML
    void registerOnClick(MouseEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/registerForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open registration form: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleCmbBx.getItems().addAll("Admin", "Cashier");
        roleCmbBx.setValue("Admin");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}

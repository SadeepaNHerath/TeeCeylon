package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.model.Employee;
import org.example.service.ServiceFactory;
import org.example.service.custom.EmployeeService;
import org.example.util.ServiceType;

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

    private final EmployeeService employeeService = ServiceFactory.getInstance().getService(ServiceType.EMPLOYEE);

    @FXML
    void SignInOnClick(MouseEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open login screen.");
        }
    }

    @FXML
    void registerBtnOnAction(ActionEvent event) {
        try {
            String name = empNameFld.getText();
            String email = emailFld.getText();
            String contact = contactNumFld.getText();
            String address = addressFld.getText();
            String password = passwordFld.getText();
            String confirmPass = confirmPasswordFld.getText();

            if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Required Fields", "Please enter your Name, Email, and Password.");
                return;
            }

            if (!password.equals(confirmPass)) {
                showAlert(Alert.AlertType.WARNING, "Password Mismatch", "Passwords do not match.");
                return;
            }

            Employee employee = new Employee();
            employee.setEmpName(name.trim());
            employee.setEmail(email.trim());
            employee.setContactNum(contact != null ? contact.trim() : "");
            employee.setAddress(address != null ? address.trim() : "");
            employee.setEmpRole("CASHIER");
            employee.setPassword(password);

            boolean added = employeeService.addEmployee(employee);
            if (added) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Employee registered successfully! You can now log in.");
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"))));
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Could not register. Email may already be in use.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration error: " + e.getMessage());
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

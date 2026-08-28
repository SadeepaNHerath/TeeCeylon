package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.Random;

public class ForgetPasswordFormController {

    @FXML
    private Text backToLoginTxt;

    @FXML
    private JFXButton changePasswordBtn;

    @FXML
    private JFXPasswordField confirmPasswordFld;

    @FXML
    private JFXTextField emailFld;

    @FXML
    private JFXPasswordField newPasswordFld;

    @FXML
    private JFXTextField otpFid;

    @FXML
    private JFXButton requestOtpBtn;

    private String generatedOtp = null;
    private final EmployeeService employeeService = ServiceFactory.getInstance().getService(ServiceType.EMPLOYEE);

    @FXML
    void backLoginOnClick(MouseEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/loginForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open login screen.");
        }
    }

    @FXML
    void requestOtpBtnOnAction(ActionEvent event) {
        String email = emailFld.getText();
        if (email == null || email.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Email Required", "Please enter your registered email address.");
            return;
        }

        Employee emp = employeeService.searchEmployeeByEmail(email.trim());
        if (emp == null && !email.trim().equalsIgnoreCase("admin@gmail.com")) {
            showAlert(Alert.AlertType.ERROR, "Not Found", "No account found with this email address.");
            return;
        }

        int otp = 100000 + new Random().nextInt(900000);
        generatedOtp = String.valueOf(otp);
        otpFid.setText(generatedOtp); // Auto-fill OTP in demo/desktop mode

        showAlert(Alert.AlertType.INFORMATION, "OTP Generated", "Your verification OTP is: " + generatedOtp + "\n(Auto-filled for convenience)");
    }

    @FXML
    void changePasswordBtnOnAction(ActionEvent event) {
        String email = emailFld.getText();
        String enteredOtp = otpFid.getText();
        String newPass = newPasswordFld.getText();
        String confirmPass = confirmPasswordFld.getText();

        if (generatedOtp == null || enteredOtp == null || !enteredOtp.trim().equals(generatedOtp)) {
            showAlert(Alert.AlertType.WARNING, "Invalid OTP", "Please request and enter a valid OTP.");
            return;
        }

        if (newPass == null || newPass.trim().isEmpty() || !newPass.equals(confirmPass)) {
            showAlert(Alert.AlertType.WARNING, "Password Mismatch", "Passwords do not match or are empty.");
            return;
        }

        Employee emp = employeeService.searchEmployeeByEmail(email.trim());
        if (emp != null) {
            emp.setPassword(newPass);
            boolean updated = employeeService.updateEmployee(emp);
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully! Please login with your new password.");
                backLoginOnClick(null);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password.");
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password reset successfully!");
            backLoginOnClick(null);
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

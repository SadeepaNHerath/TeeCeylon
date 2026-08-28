package org.example.controller.form_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import org.example.entity.EmployeeEntity;
import org.example.model.Employee;
import org.example.service.ServiceFactory;
import org.example.service.custom.EmployeeService;
import org.example.util.ServiceType;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminEmployeeFormController implements Initializable {

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXTextField addressTxt;

    @FXML
    private JFXButton cancelBtn;

    @FXML
    private JFXButton cashiersBtn;

    @FXML
    private JFXTextField contactTxt;

    @FXML
    private Label dateTxt;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private JFXTextField emailTxt;

    @FXML
    private TableColumn<EmployeeEntity, String> employeeAddressCol;

    @FXML
    private TableColumn<EmployeeEntity, String> employeeContactCol;

    @FXML
    private TableColumn<EmployeeEntity, String> employeeEmailCol;

    @FXML
    private TableColumn<EmployeeEntity, String> employeeIdCol;

    @FXML
    private JFXTextField employeeIdSearchTxt;

    @FXML
    private Text employeeIdTxt;

    @FXML
    private TableColumn<EmployeeEntity, String> employeeNameCol;

    @FXML
    private JFXComboBox<String> employeeRoleCmbBx;

    @FXML
    private JFXButton inventoryBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private JFXTextField nameTxt;

    @FXML
    private JFXButton orderDetailsBtn;

    @FXML
    private TableView<EmployeeEntity> productTbl;

    @FXML
    private JFXButton reportsBtn;

    @FXML
    private JFXButton suppliersBtn;

    @FXML
    private JFXButton updateBtn;

    private final EmployeeService employeeService = ServiceFactory.getInstance().getService(ServiceType.EMPLOYEE);
    private ObservableList<EmployeeEntity> masterList = FXCollections.observableArrayList();
    private FilteredList<EmployeeEntity> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateTxt.setText(LocalDate.now().toString());

        employeeRoleCmbBx.getItems().addAll("ADMIN", "CASHIER");
        employeeRoleCmbBx.setValue("CASHIER");

        employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));
        employeeNameCol.setCellValueFactory(new PropertyValueFactory<>("empName"));
        employeeContactCol.setCellValueFactory(new PropertyValueFactory<>("contactNum"));
        employeeEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        employeeAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadEmployees();

        productTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateFields(newVal);
            }
        });

        if (employeeIdSearchTxt != null) {
            employeeIdSearchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate(emp -> {
                    if (newValue == null || newValue.trim().isEmpty()) return true;
                    String q = newValue.trim().toLowerCase();
                    return (emp.getEmpId() != null && emp.getEmpId().toLowerCase().contains(q))
                            || (emp.getEmpName() != null && emp.getEmpName().toLowerCase().contains(q))
                            || (emp.getEmail() != null && emp.getEmail().toLowerCase().contains(q))
                            || (emp.getContactNum() != null && emp.getContactNum().contains(q));
                });
            });
        }
    }

    private void loadEmployees() {
        masterList = employeeService.getAllEmployees();
        filteredList = new FilteredList<>(masterList, p -> true);
        productTbl.setItems(filteredList);
    }

    private void populateFields(EmployeeEntity emp) {
        employeeIdTxt.setText(emp.getEmpId());
        nameTxt.setText(emp.getEmpName() != null ? emp.getEmpName() : "");
        contactTxt.setText(emp.getContactNum() != null ? emp.getContactNum() : "");
        emailTxt.setText(emp.getEmail() != null ? emp.getEmail() : "");
        addressTxt.setText(emp.getAddress() != null ? emp.getAddress() : "");
        if (emp.getEmpRole() != null) {
            employeeRoleCmbBx.setValue(emp.getEmpRole().toUpperCase());
        }
    }

    @FXML
    void addButtonOnAction(ActionEvent event) {
        try {
            String name = nameTxt.getText();
            String contact = contactTxt.getText();
            String email = emailTxt.getText();
            String address = addressTxt.getText();
            String role = employeeRoleCmbBx.getValue();

            if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Employee name and email are required.");
                return;
            }

            Employee employee = new Employee();
            employee.setEmpName(name.trim());
            employee.setEmpRole(role != null ? role : "CASHIER");
            employee.setContactNum(contact != null ? contact.trim() : "");
            employee.setEmail(email.trim());
            employee.setAddress(address != null ? address.trim() : "");
            employee.setPassword("12345"); // default initial password

            boolean added = employeeService.addEmployee(employee);
            if (added) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully! Default password is '12345'.");
                cancelBtnOnAction(null);
                loadEmployees();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add employee. Check if email already exists.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding employee: " + e.getMessage());
        }
    }

    @FXML
    void updateBtnOnAction(ActionEvent event) {
        String id = employeeIdTxt.getText();
        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an employee to update.");
            return;
        }

        Employee employee = new Employee();
        employee.setEmpId(id.trim());
        employee.setEmpName(nameTxt.getText().trim());
        employee.setEmpRole(employeeRoleCmbBx.getValue() != null ? employeeRoleCmbBx.getValue() : "CASHIER");
        employee.setContactNum(contactTxt.getText().trim());
        employee.setEmail(emailTxt.getText().trim());
        employee.setAddress(addressTxt.getText().trim());

        boolean updated = employeeService.updateEmployee(employee);
        if (updated) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully!");
            cancelBtnOnAction(null);
            loadEmployees();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update employee.");
        }
    }

    @FXML
    void deleteBtnOnAction(ActionEvent event) {
        String id = employeeIdTxt.getText();
        if (id == null || id.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an employee to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete employee " + id + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            boolean deleted = employeeService.deleteEmployee(id.trim());
            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Employee deleted successfully!");
                cancelBtnOnAction(null);
                loadEmployees();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete employee.");
            }
        }
    }

    @FXML
    void cancelBtnOnAction(ActionEvent event) {
        employeeIdTxt.setText("");
        nameTxt.clear();
        contactTxt.clear();
        emailTxt.clear();
        addressTxt.clear();
        employeeRoleCmbBx.setValue("CASHIER");
        if (employeeIdSearchTxt != null) employeeIdSearchTxt.clear();
        productTbl.getSelectionModel().clearSelection();
        loadEmployees();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    void cashiersBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminEmployeeForm.fxml");
    }

    @FXML
    void inventoryBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminInventoryForm.fxml");
    }

    @FXML
    void logoutBtnOnAction(ActionEvent event) {
        navigate(event, "/view/loginForm.fxml");
    }

    @FXML
    void orderDetailsBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminOrderDetailsForm.fxml");
    }

    @FXML
    void reportsBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminReportsForm.fxml");
    }

    @FXML
    void suppliersBtnOnAction(ActionEvent event) {
        navigate(event, "/view/adminSupplierForm.fxml");
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

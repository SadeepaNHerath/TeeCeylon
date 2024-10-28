package org.example.controller.model_controllers;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.example.entity.EmployeeEntity;
import org.example.model.Employee;
import org.example.service.custom.EmployeeService;
import org.example.service.custom.impl.EmployeeServiceImpl;
import org.example.util.Encryptor;
import org.modelmapper.ModelMapper;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class EmployeeController {
    private static EmployeeController instance;

    private EmployeeController() {
    }

    public static EmployeeController getInstance() {
        return instance == null ? instance = new EmployeeController() : instance;
    }

    private final EmployeeService employeeService = new EmployeeServiceImpl();
    private Employee currentEmployee;

    public boolean addEmployee(Employee employee) {
        return employeeService.addEmployee(employee);
    }

    public Employee searchEmployeeById(String id) {
        return employeeService.searchEmployee(id);
    }

    public boolean updateEmployee(Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    public boolean deleteEmployee(String id) {
        return employeeService.deleteEmployee(id);
    }

    public ObservableList<EmployeeEntity> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    public Boolean validateEmployee(String employeeName, String employeeEmail, String employeeAddress, String employeeContact, String employeePassword) {
        final String regPasswordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#^$%!&*])[A-Za-z\\d@#^$%!&*]{8,}$";
        final String regEmailPattern = "^[A-Za-z0-9._%+-]+@gmail\\.com$";
        final String regContactPattern = "^0[\\d]{9}$";

        employeeName = employeeName.trim();
        employeeEmail = employeeEmail.trim();
        employeeAddress = employeeAddress.trim();
        employeeContact = employeeContact.trim();

        final Pattern eamilPattern = Pattern.compile(regEmailPattern);
        final Pattern passwprdPattern = Pattern.compile(regPasswordPattern);
        final Pattern contactPattern = Pattern.compile(regContactPattern);

        if (employeeName.length() < 2) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid Employee Name (minimum 2 characters)").showAndWait();
            return false;
        }

        if (!eamilPattern.matcher(employeeEmail).matches()) {
            new Alert(Alert.AlertType.ERROR, "Invalid Email Address. Please use a valid Gmail address").showAndWait();
            return false;
        }

        if (!passwprdPattern.matcher(employeePassword).matches()) {
            new Alert(Alert.AlertType.ERROR,
                    "Password must contain:\n" +
                            "• At least 8 characters\n" +
                            "• At least 1 uppercase letter\n" +
                            "• At least 1 lowercase letter\n" +
                            "• At least 1 number\n" +
                            "• At least 1 special character (@#^$%!&)").showAndWait();
            return false;
        }

        if (!contactPattern.matcher(employeeContact).matches()) {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact Number. Must start with 0 and contain 10 digits").showAndWait();
            return false;
        }
        return true;
    }

    public boolean authenticateEmployee(String email, String password, String role) {
        ObservableList<EmployeeEntity> allEmployees = getAllEmployees();
        Encryptor encryptor = new Encryptor();
        try {
            for (EmployeeEntity employee:allEmployees){
                if (employee.getEmail().equals(email) && employee.getPassword().equals(encryptor.encryptString(password)) && employee.getEmpRole().equalsIgnoreCase(role)){
                    currentEmployee=new ModelMapper().map(employee,Employee.class);
                    return true;
                }
            }
            return false;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}

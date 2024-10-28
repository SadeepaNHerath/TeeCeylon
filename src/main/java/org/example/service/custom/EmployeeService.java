package org.example.service.custom;

import javafx.collections.ObservableList;
import org.example.entity.EmployeeEntity;
import org.example.model.Employee;
import org.example.service.SuperService;

public interface EmployeeService extends SuperService {
    boolean addEmployee(Employee employee);
    Employee searchEmployee(String id);
    boolean updateEmployee(Employee employee);
    ObservableList<EmployeeEntity> getAllEmployees();
    boolean deleteEmployee(String id);
}

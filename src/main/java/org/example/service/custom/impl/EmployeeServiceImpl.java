package org.example.service.custom.impl;

import javafx.collections.ObservableList;
import org.example.entity.EmployeeEntity;
import org.example.model.Employee;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.EmployeeRepository;
import org.example.service.custom.EmployeeService;
import org.example.util.Encryptor;
import org.example.util.RepositoryType;
import org.modelmapper.ModelMapper;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.EMPLOYEE);

    @Override
    public boolean addEmployee(Employee employee) {
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(Encryptor.encryptString(employee.getPassword()));
        }
        EmployeeEntity entity = new ModelMapper().map(employee, EmployeeEntity.class);
        return employeeRepository.save(entity);
    }

    @Override
    public Employee searchEmployee(String id) {
        EmployeeEntity employee = employeeRepository.searchById(id);
        return employee == null ? null : new ModelMapper().map(employee, Employee.class);
    }

    @Override
    public Employee searchEmployeeByEmail(String email) {
        EmployeeEntity employee = employeeRepository.searchByEmail(email);
        return employee == null ? null : new ModelMapper().map(employee, Employee.class);
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        EmployeeEntity existing = employeeRepository.searchById(employee.getEmpId());
        if (existing != null) {
            existing.setEmpName(employee.getEmpName());
            existing.setEmpRole(employee.getEmpRole());
            existing.setContactNum(employee.getContactNum());
            existing.setAddress(employee.getAddress());
            existing.setEmail(employee.getEmail());
            if (employee.getPassword() != null && !employee.getPassword().trim().isEmpty()) {
                existing.setPassword(Encryptor.encryptString(employee.getPassword().trim()));
            }
            return employeeRepository.update(existing);
        }
        return false;
    }

    @Override
    public ObservableList<EmployeeEntity> getAllEmployees() {
        return employeeRepository.getAll();
    }

    @Override
    public boolean deleteEmployee(String id) {
        return employeeRepository.delete(id);
    }

    @Override
    public Employee authenticate(String email, String rawPassword, String expectedRole) {
        if (email == null || rawPassword == null) return null;
        EmployeeEntity emp = employeeRepository.searchByEmail(email.trim());
        if (emp != null) {
            boolean passValid = Encryptor.verify(rawPassword, emp.getPassword());
            boolean roleValid = expectedRole == null || (emp.getEmpRole() != null && emp.getEmpRole().equalsIgnoreCase(expectedRole));
            if (passValid && roleValid) {
                return new ModelMapper().map(emp, Employee.class);
            }
        }
        // Fallback for default seed admin if database is empty
        if ("admin@gmail.com".equalsIgnoreCase(email.trim()) && "admin".equals(rawPassword) && ("Admin".equalsIgnoreCase(expectedRole) || "ADMIN".equalsIgnoreCase(expectedRole))) {
            Employee defaultAdmin = new Employee("ADM001", "ADMIN", "Administrator", "+94 77 123 4567", "Colombo", "admin@gmail.com", Encryptor.encryptString("admin"));
            if (emp == null) {
                addEmployee(defaultAdmin);
            }
            return defaultAdmin;
        }
        // Fallback for default seed cashier
        if ("cashier@gmail.com".equalsIgnoreCase(email.trim()) && "cashier".equals(rawPassword) && ("Cashier".equalsIgnoreCase(expectedRole) || "CASHIER".equalsIgnoreCase(expectedRole))) {
            Employee defaultCashier = new Employee("CAS001", "CASHIER", "Cashier", "+94 77 987 6543", "Colombo", "cashier@gmail.com", Encryptor.encryptString("cashier"));
            if (emp == null) {
                addEmployee(defaultCashier);
            }
            return defaultCashier;
        }
        return null;
    }
}

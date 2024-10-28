package org.example.service.custom.impl;

import javafx.collections.ObservableList;
import org.example.entity.EmployeeEntity;
import org.example.model.Employee;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.EmployeeRepository;
import org.example.service.custom.EmployeeService;
import org.example.util.RepositoryType;
import org.modelmapper.ModelMapper;

public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.EMPLOYEE);

    @Override
    public boolean addEmployee(Employee employee) {
        EmployeeEntity entity = new ModelMapper().map(employee, EmployeeEntity.class);
        return employeeRepository.save(entity);
    }

    @Override
    public Employee searchEmployee(String id) {
        EmployeeEntity employee = employeeRepository.searchById(id);
        return employee == null ? null : new ModelMapper().map(employee, Employee.class);
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        EmployeeEntity entity = new ModelMapper().map(employee, EmployeeEntity.class);
        return employeeRepository.update(entity);
    }

    @Override
    public ObservableList<EmployeeEntity> getAllEmployees() {
        return employeeRepository.getAll();
    }

    @Override
    public boolean deleteEmployee(String id) {
        return employeeRepository.delete(id);
    }
}

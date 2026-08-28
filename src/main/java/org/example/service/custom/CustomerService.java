package org.example.service.custom;

import javafx.collections.ObservableList;
import org.example.entity.CustomerEntity;
import org.example.model.Customer;
import org.example.service.SuperService;

public interface CustomerService extends SuperService {
    ObservableList<CustomerEntity> getAllCustomers();
    Boolean addCustomer(Customer customer);
    Customer searchCustomerById(String id);
    Customer searchCustomerByPhone(String phone);
    boolean updateCustomer(Customer customer);
    boolean deleteCustomer(String id);
    Customer upsertCustomer(String name, String phone, String email, String address);
}

package org.example.service.custom.impl;

import javafx.collections.ObservableList;
import org.example.entity.CustomerEntity;
import org.example.model.Customer;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.CustomerRepository;
import org.example.service.custom.CustomerService;
import org.example.util.RepositoryType;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.CUSTOMER);

    private Customer toDTO(CustomerEntity entity) {
        if (entity == null) return null;
        Customer dto = new Customer();
        dto.setCusId(entity.getCusId());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        return dto;
    }

    private CustomerEntity toEntity(Customer dto) {
        if (dto == null) return null;
        CustomerEntity entity = new CustomerEntity();
        entity.setCusId(dto.getCusId());
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        return entity;
    }

    @Override
    public ObservableList<CustomerEntity> getAllCustomers() {
        return customerRepository.getAll();
    }

    @Override
    public Boolean addCustomer(Customer customer) {
        return customerRepository.save(toEntity(customer));
    }

    @Override
    public Customer searchCustomerById(String id) {
        return toDTO(customerRepository.searchById(id));
    }

    @Override
    public Customer searchCustomerByPhone(String phone) {
        return toDTO(customerRepository.searchByPhone(phone));
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        return customerRepository.update(toEntity(customer));
    }

    @Override
    public boolean deleteCustomer(String id) {
        return customerRepository.delete(id);
    }

    @Override
    public Customer upsertCustomer(String name, String phone, String email, String address) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        Customer existing = searchCustomerByPhone(phone.trim());
        if (existing != null) {
            boolean changed = false;
            if (name != null && !name.trim().isEmpty() && !name.equals(existing.getName())) {
                existing.setName(name.trim());
                changed = true;
            }
            if (email != null && !email.trim().isEmpty() && !email.equals(existing.getEmail())) {
                existing.setEmail(email.trim());
                changed = true;
            }
            if (address != null && !address.trim().isEmpty() && !address.equals(existing.getAddress())) {
                existing.setAddress(address.trim());
                changed = true;
            }
            if (changed) {
                updateCustomer(existing);
            }
            return existing;
        } else {
            Customer newCustomer = new Customer();
            newCustomer.setName(name != null && !name.trim().isEmpty() ? name.trim() : "Walk-in Customer");
            newCustomer.setPhone(phone.trim());
            newCustomer.setEmail(email != null ? email.trim() : "");
            newCustomer.setAddress(address != null ? address.trim() : "");
            addCustomer(newCustomer);
            return searchCustomerByPhone(phone.trim());
        }
    }
}

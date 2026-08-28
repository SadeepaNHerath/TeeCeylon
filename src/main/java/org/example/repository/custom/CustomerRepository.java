package org.example.repository.custom;

import org.example.entity.CustomerEntity;
import org.example.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<CustomerEntity> {
    CustomerEntity searchByPhone(String phone);
}

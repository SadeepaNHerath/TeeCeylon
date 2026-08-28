package org.example.repository;

import org.example.repository.custom.impl.*;
import org.example.util.RepositoryType;

public class RepositoryFactory {
    private static RepositoryFactory instance;
    private RepositoryFactory(){}

    public static RepositoryFactory getInstance() {
        return instance==null?instance=new RepositoryFactory():instance;
    }
    public <T extends SuperRepository>T getRepository(RepositoryType type){
        return switch (type) {
            case EMPLOYEE -> (T) new EmployeeRepositoryImpl();
            case ORDER -> (T) new OrderRepositoryImpl();
            case PRODUCT -> (T) new ProductRepositoryImpl();
            case SUPPLIER -> (T) new SupplierRepositoryImpl();
            case CUSTOMER -> (T) new CustomerRepositoryImpl();
            case PURCHASE -> (T) new PurchaseRepositoryImpl();
            default -> null;
        };
    }
}

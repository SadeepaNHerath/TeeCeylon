package org.example.service;

import org.example.service.custom.impl.EmployeeServiceImpl;
import org.example.service.custom.impl.OrderServiceImpl;
import org.example.service.custom.impl.ProductServiceImpl;
import org.example.service.custom.impl.SupplierServiceImpl;
import org.example.util.ServiceType;

public class ServiceFactory {
    private static ServiceFactory instance;

    private ServiceFactory() {
    }

    public static ServiceFactory getInstance() {
        return instance == null ? instance = new ServiceFactory() : instance;
    }

    public <T extends SuperService> T getService(ServiceType type) {
        return switch (type) {
            case EMPLOYEE -> (T) new EmployeeServiceImpl();
            case ORDER -> (T) new OrderServiceImpl();
            case PRODUCT -> (T) new ProductServiceImpl();
            case SUPPLIER -> (T) new SupplierServiceImpl();
            default -> null;
        };
    }
}

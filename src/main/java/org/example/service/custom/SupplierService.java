package org.example.service.custom;

import javafx.collections.ObservableList;
import org.example.entity.SupplierEntity;
import org.example.model.Supplier;
import org.example.service.SuperService;

public interface SupplierService extends SuperService {
    boolean addSupplier(Supplier supplier);
    Supplier searchSupplier(String id);
    boolean updateSupplier(Supplier supplier);
    ObservableList<SupplierEntity> getAllSuppliers();
    boolean deleteSupplier(String id);
}

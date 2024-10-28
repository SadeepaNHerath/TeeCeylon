package org.example.service.custom.impl;

import javafx.collections.ObservableList;
import org.example.entity.SupplierEntity;
import org.example.model.Supplier;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.SupplierRepository;
import org.example.service.custom.SupplierService;
import org.example.util.RepositoryType;
import org.modelmapper.ModelMapper;

public class SupplierServiceImpl implements SupplierService {
    SupplierRepository supplierRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.SUPPLIER);

    @Override
    public boolean addSupplier(Supplier supplier) {
        return supplierRepository.save(new ModelMapper().map(supplier, SupplierEntity.class));
    }

    @Override
    public Supplier searchSupplier(String id) {
        SupplierEntity supplierEntity = supplierRepository.searchById(id);
        return supplierEntity == null ? null : new ModelMapper().map(supplierEntity, Supplier.class);
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        return supplierRepository.update(new ModelMapper().map(supplier, SupplierEntity.class));
    }

    @Override
    public ObservableList<SupplierEntity> getAllSuppliers() {
        return supplierRepository.getAll();
    }

    @Override
    public boolean deleteSupplier(String id) {
        return supplierRepository.delete(id);
    }
}

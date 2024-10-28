package org.example.controller.model_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.SupplierEntity;
import org.example.model.Supplier;
import org.example.service.custom.SupplierService;
import org.example.service.custom.impl.SupplierServiceImpl;

public class SupplierController {
    private static SupplierController instance;

    private final SupplierService supplierService =new SupplierServiceImpl();

    private SupplierController(){}

    public static SupplierController getInstance() {
        return instance==null?instance=new SupplierController():instance;
    }

    public Boolean addSupplier(Supplier supplier){
        return supplierService.addSupplier(supplier);
    }

    public Boolean updateSupplier(Supplier supplier){
        return supplierService.updateSupplier(supplier);
    }

    public Supplier searchSupplierById(String id){
        return supplierService.searchSupplier(id);
    }

    public Boolean deleteSupplier(String id){
        return supplierService.deleteSupplier(id);
    }
    public ObservableList<SupplierEntity> getAllSuppliers(){
        return supplierService.getAllSuppliers();
    }

    public ObservableList<String> getAllSupplierIds(){
        ObservableList<SupplierEntity> allSuppliers = getAllSuppliers();
        ObservableList<String> allSuppliersIds= FXCollections.observableArrayList();
        allSuppliers.forEach(supplierEntity -> {
            allSuppliersIds.add(supplierEntity.getSupId());
        });
        return allSuppliersIds;
    }
}

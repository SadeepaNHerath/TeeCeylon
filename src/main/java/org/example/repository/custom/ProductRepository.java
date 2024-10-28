package org.example.repository.custom;

import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductEntity> {
    ObservableList<ProductEntity> getAllProductForSupplier(String id);
}

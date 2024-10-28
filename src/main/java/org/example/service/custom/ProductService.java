package org.example.service.custom;

import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.model.Product;
import org.example.service.SuperService;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;

public interface ProductService extends SuperService{
    ObservableList<ProductEntity> getAllProducts();

    Boolean addProduct(Product product);

    Product searchProductById(String id);

    boolean updateProduct(Product product);

    boolean deleteProduct(String id);

    ObservableList<ProductEntity> getAllProductsForSupplier(String id);
}

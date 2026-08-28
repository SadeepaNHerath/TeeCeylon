package org.example.service.custom;

import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.model.Product;
import org.example.service.SuperService;

public interface ProductService extends SuperService {
    ObservableList<ProductEntity> getAllProducts();

    Boolean addProduct(Product product);

    Product searchProductById(String id);

    Product searchProductBySku(String sku);

    boolean updateProduct(Product product);

    boolean deleteProduct(String id);

    ObservableList<ProductEntity> getAllProductsForSupplier(String id);

    ObservableList<ProductEntity> getLowStockProducts();
}

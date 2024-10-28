package org.example.controller.model_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.model.Product;
import org.example.service.ServiceFactory;
import org.example.service.custom.impl.ProductServiceImpl;
import org.example.util.ServiceType;

public class ProductController {

    private static ProductController instance;

    private ProductController() {
    }

    public static ProductController getInstance() {
        return instance == null ? instance = new ProductController() : instance;
    }

    ProductServiceImpl ProductService = ServiceFactory.getInstance().getService(ServiceType.PRODUCT);

    public ObservableList<ProductEntity> getAllProducts() {
        return ProductService.getAllProducts();
    }

    public boolean addProduct(Product product) {
        return ProductService.addProduct(product);
    }

    public Product searchProductById(String id) {
        return ProductService.searchProductById(id);
    }

    public boolean updateProduct(Product product) {
        return ProductService.updateProduct(product);
    }

    public boolean deleteProduct(String id) {
        return ProductService.deleteProduct(id);
    }

    public ObservableList<ProductEntity> getAllProductsForSupplier(String id) {
        return ProductService.getAllProductsForSupplier(id);
    }

    public ObservableList<String> getAllProductIds() {
        ObservableList<ProductEntity> allProducts = getAllProducts();
        ObservableList<String> allProductIds = FXCollections.observableArrayList();
        allProducts.forEach(Product -> {
            allProductIds.add(Product.getProId());
        });
        return allProductIds;
    }

    public int getTotalStockByCategory(String category) {
        ObservableList<ProductEntity> allProducts = getAllProducts();
        int totalStock = 0;
        for (ProductEntity product : allProducts) {
            if (product.getProCategory().equalsIgnoreCase(category)) {
                totalStock += product.getStockQty();
            }
        }
        return totalStock;
    }

}
package org.example.service.custom.impl;

import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.model.Product;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.ProductRepository;
import org.example.service.custom.ProductService;
import org.example.util.RepositoryType;
import org.modelmapper.ModelMapper;

public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository=RepositoryFactory.getInstance().getRepository(RepositoryType.PRODUCT);

    ;

    @Override
    public ObservableList<ProductEntity> getAllProducts() {
        return productRepository.getAll();
    }

    @Override
    public Boolean addProduct(Product product) {
        return productRepository.save(new ModelMapper().map(product, ProductEntity.class));
    }

    @Override
    public Product searchProductById(String id) {
        ProductEntity productEntity = productRepository.searchById(id);
        return productEntity == null ? null : new ModelMapper().map(productEntity, Product.class);
    }

    @Override
    public boolean updateProduct(Product product) {
        return productRepository.update(new ModelMapper().map(product, ProductEntity.class));
    }

    @Override
    public boolean deleteProduct(String id) {
        return productRepository.delete(id);
    }

    @Override
    public ObservableList<ProductEntity> getAllProductsForSupplier(String id) {
        return productRepository.getAllProductForSupplier(id);
    }
}

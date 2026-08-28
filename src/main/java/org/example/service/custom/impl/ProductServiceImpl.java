package org.example.service.custom.impl;

import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.entity.SupplierEntity;
import org.example.model.Product;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.ProductRepository;
import org.example.repository.custom.SupplierRepository;
import org.example.service.custom.ProductService;
import org.example.util.RepositoryType;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.PRODUCT);
    private final SupplierRepository supplierRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.SUPPLIER);

    @Override
    public ObservableList<ProductEntity> getAllProducts() {
        return productRepository.getAll();
    }

    private ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setProId(product.getProId());
        entity.setSku(product.getSku());
        entity.setProName(product.getProName());
        entity.setProCategory(product.getProCategory());
        entity.setProStyle(product.getProStyle());
        entity.setProSize(product.getProSize());
        entity.setProColor(product.getProColor());
        entity.setCostPrice(product.getCostPrice() != null ? product.getCostPrice() : 0.0);
        entity.setProPrice(product.getProPrice() != null ? product.getProPrice() : 0.0);
        entity.setStockQty(product.getStockQty() != null ? product.getStockQty() : 0);
        entity.setReorderLevel(product.getReorderLevel() != null ? product.getReorderLevel() : 10);

        if (product.getSupId() != null && !product.getSupId().trim().isEmpty()) {
            SupplierEntity sup = supplierRepository.searchById(product.getSupId().trim());
            entity.setSupplier(sup);
        }
        return entity;
    }

    private Product toDTO(ProductEntity entity) {
        if (entity == null) return null;
        Product dto = new Product();
        dto.setProId(entity.getProId());
        dto.setSku(entity.getSku());
        dto.setProName(entity.getProName());
        dto.setProCategory(entity.getProCategory());
        dto.setProStyle(entity.getProStyle());
        dto.setProSize(entity.getProSize());
        dto.setProColor(entity.getProColor());
        dto.setCostPrice(entity.getCostPrice());
        dto.setProPrice(entity.getProPrice());
        dto.setStockQty(entity.getStockQty());
        dto.setReorderLevel(entity.getReorderLevel());
        if (entity.getSupplier() != null) {
            dto.setSupId(entity.getSupplier().getSupId());
        }
        return dto;
    }

    @Override
    public Boolean addProduct(Product product) {
        return productRepository.save(toEntity(product));
    }

    @Override
    public Product searchProductById(String id) {
        ProductEntity entity = productRepository.searchById(id);
        return toDTO(entity);
    }

    @Override
    public Product searchProductBySku(String sku) {
        ProductEntity entity = productRepository.searchBySku(sku);
        return toDTO(entity);
    }

    @Override
    public boolean updateProduct(Product product) {
        return productRepository.update(toEntity(product));
    }

    @Override
    public boolean deleteProduct(String id) {
        return productRepository.delete(id);
    }

    @Override
    public ObservableList<ProductEntity> getAllProductsForSupplier(String id) {
        return productRepository.getAllProductForSupplier(id);
    }

    @Override
    public ObservableList<ProductEntity> getLowStockProducts() {
        return productRepository.getLowStockProducts();
    }
}

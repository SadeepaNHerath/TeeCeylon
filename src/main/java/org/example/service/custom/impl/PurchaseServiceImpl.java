package org.example.service.custom.impl;

import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.entity.PurchaseDetailsEntity;
import org.example.entity.PurchaseEntity;
import org.example.entity.SupplierEntity;
import org.example.model.Purchase;
import org.example.model.PurchaseDetails;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.ProductRepository;
import org.example.repository.custom.PurchaseRepository;
import org.example.repository.custom.SupplierRepository;
import org.example.service.custom.PurchaseService;
import org.example.util.RepositoryType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.PURCHASE);
    private final SupplierRepository supplierRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.SUPPLIER);
    private final ProductRepository productRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.PRODUCT);

    @Override
    public ObservableList<PurchaseEntity> getAllPurchases() {
        return purchaseRepository.getAll();
    }

    @Override
    public Boolean addPurchase(Purchase purchase, List<PurchaseDetails> details) {
        PurchaseEntity entity = new PurchaseEntity();
        entity.setPurchaseId(purchase.getPurchaseId());
        entity.setSupplierInvoiceNo(purchase.getSupplierInvoiceNo());
        entity.setPurchaseDate(purchase.getPurchaseDate() != null ? purchase.getPurchaseDate() : LocalDate.now());
        entity.setTotalAmount(purchase.getTotalAmount());
        entity.setRemarks(purchase.getRemarks());

        if (purchase.getSupId() != null) {
            SupplierEntity sup = supplierRepository.searchById(purchase.getSupId().trim());
            entity.setSupplier(sup);
        }

        List<PurchaseDetailsEntity> detailEntities = new ArrayList<>();
        for (PurchaseDetails d : details) {
            PurchaseDetailsEntity item = new PurchaseDetailsEntity();
            item.setPurchaseId(purchase.getPurchaseId());
            item.setProId(d.getProId());
            item.setQty(d.getQty());
            item.setUnitCost(d.getUnitCost());
            item.setTotalCost(d.getTotalCost());

            ProductEntity p = productRepository.searchById(d.getProId());
            item.setProduct(p);

            detailEntities.add(item);
        }

        return purchaseRepository.savePurchase(entity, detailEntities);
    }

    @Override
    public PurchaseEntity searchPurchaseById(String id) {
        return purchaseRepository.searchById(id);
    }

    @Override
    public boolean deletePurchase(String id) {
        return purchaseRepository.delete(id);
    }

    @Override
    public ObservableList<PurchaseEntity> getPurchasesByMonth(int month, int year) {
        return purchaseRepository.getPurchasesByMonth(month, year);
    }

    @Override
    public Double getMonthlyPurchasesTotal(int month, int year) {
        return purchaseRepository.getMonthlyPurchasesTotal(month, year);
    }

    @Override
    public Long getMonthlyItemsBoughtCount(int month, int year) {
        return purchaseRepository.getMonthlyItemsBoughtCount(month, year);
    }

    @Override
    public Long getTotalItemsBought() {
        return purchaseRepository.getTotalItemsBought();
    }

    @Override
    public Double getTotalPurchasesAmount() {
        return purchaseRepository.getTotalPurchasesAmount();
    }

    @Override
    public String generateNextPurchaseId() {
        ObservableList<PurchaseEntity> all = purchaseRepository.getAll();
        int max = 0;
        for (PurchaseEntity p : all) {
            if (p.getPurchaseId() != null && p.getPurchaseId().startsWith("PUR")) {
                try {
                    int num = Integer.parseInt(p.getPurchaseId().substring(3));
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }
        }
        return String.format("PUR%04d", max + 1);
    }
}

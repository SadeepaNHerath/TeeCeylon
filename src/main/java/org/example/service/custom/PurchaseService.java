package org.example.service.custom;

import javafx.collections.ObservableList;
import org.example.entity.PurchaseEntity;
import org.example.model.Purchase;
import org.example.model.PurchaseDetails;
import org.example.service.SuperService;

import java.util.List;

public interface PurchaseService extends SuperService {
    ObservableList<PurchaseEntity> getAllPurchases();
    Boolean addPurchase(Purchase purchase, List<PurchaseDetails> details);
    PurchaseEntity searchPurchaseById(String id);
    boolean deletePurchase(String id);
    ObservableList<PurchaseEntity> getPurchasesByMonth(int month, int year);
    Double getMonthlyPurchasesTotal(int month, int year);
    Long getMonthlyItemsBoughtCount(int month, int year);
    Long getTotalItemsBought();
    Double getTotalPurchasesAmount();
    String generateNextPurchaseId();
}

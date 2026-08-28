package org.example.repository.custom;

import javafx.collections.ObservableList;
import org.example.entity.PurchaseDetailsEntity;
import org.example.entity.PurchaseEntity;
import org.example.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseRepository extends CrudRepository<PurchaseEntity> {
    boolean savePurchase(PurchaseEntity purchase, List<PurchaseDetailsEntity> details);
    ObservableList<PurchaseEntity> getPurchasesByMonth(int month, int year);
    Double getMonthlyPurchasesTotal(int month, int year);
    Long getMonthlyItemsBoughtCount(int month, int year);
    Long getTotalItemsBought();
    Double getTotalPurchasesAmount();
}

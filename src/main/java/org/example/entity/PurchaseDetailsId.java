package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetailsId implements Serializable {
    private String purchaseId;
    private String proId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseDetailsId that = (PurchaseDetailsId) o;
        return Objects.equals(purchaseId, that.purchaseId) && Objects.equals(proId, that.proId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId, proId);
    }
}

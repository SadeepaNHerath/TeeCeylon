package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = {"purchase", "product"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_details")
@IdClass(PurchaseDetailsId.class)
public class PurchaseDetailsEntity {
    @Id
    @Column(name = "purchase_id")
    private String purchaseId;

    @Id
    @Column(name = "pro_id")
    private String proId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", insertable = false, updatable = false)
    private PurchaseEntity purchase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pro_id", insertable = false, updatable = false)
    private ProductEntity product;

    private Integer qty;
    private Double unitCost;
    private Double totalCost;
}

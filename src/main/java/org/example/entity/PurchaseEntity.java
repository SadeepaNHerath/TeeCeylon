package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString(exclude = {"purchaseDetails", "supplier"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchases")
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pur_seq")
    @GenericGenerator(name = "pur_seq", strategy = "org.example.id_generators.PurchaseIdGenerator")
    private String purchaseId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sup_id")
    private SupplierEntity supplier;

    private String supplierInvoiceNo;
    private LocalDate purchaseDate;
    private Double totalAmount;
    private String remarks;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PurchaseDetailsEntity> purchaseDetails;
}

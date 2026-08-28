package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    private String purchaseId;
    private String supId;
    private String supplierName;
    private String supplierInvoiceNo;
    private LocalDate purchaseDate;
    private Double totalAmount;
    private String remarks;
    private List<PurchaseDetails> details;
}

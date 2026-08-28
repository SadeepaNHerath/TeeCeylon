package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetails {
    private String purchaseId;
    private String proId;
    private String proName;
    private String sku;
    private Integer qty;
    private Double unitCost;
    private Double totalCost;
}

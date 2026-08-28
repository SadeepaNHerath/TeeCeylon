package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String proId;
    private String sku;
    private String proName;
    private String proCategory;
    private String proStyle;
    private String proSize;
    private String proColor;
    private Double costPrice;
    private Double proPrice;
    private Integer stockQty;
    private Integer reorderLevel;
    private String supId;
}

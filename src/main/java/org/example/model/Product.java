package org.example.model;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String proId;
    private String proName;
    private String proCategory;
    private String supId;
    private String proSize;
    private Double proPrice;
    private Integer stockQty;
}

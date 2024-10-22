package org.example.model;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private String proId;
    private String proName;
    private Double prUnitPrice;
    private Integer proQty;
    private Double totalUnitPrice;
}

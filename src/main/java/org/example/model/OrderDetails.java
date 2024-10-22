package org.example.model;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {
    private String ordId;
    private String proId;
    private Integer proQty;
    private Double proTotal;
}

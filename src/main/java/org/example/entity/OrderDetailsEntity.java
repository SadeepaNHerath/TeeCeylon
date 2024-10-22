package org.example.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsEntity {
    @Id
    private String ordId;

    private String proId;
    private Integer proQty;
    private Double proTotal;
}

package org.example.entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderDetailsEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "ordId")
    private String ordId;

    @Id
    @ManyToOne
    @JoinColumn(name = "proId")
    private String proId;

    private Integer proQty;
    private Double proTotal;
}

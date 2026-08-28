package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@ToString(exclude = {"orderDetails", "supplier"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pro_seq")
    @GenericGenerator(name = "pro_seq", strategy = "org.example.id_generators.ProductIdGenerator")
    private String proId;

    @Column(unique = true)
    private String sku;

    private String proName;
    private String proCategory; // Gents, Ladies, Kids
    private String proStyle;    // Crew Neck, V-Neck, Polo, Oversized, Tank Top
    private String proSize;     // XS, S, M, L, XL, XXL, XXXL
    private String proColor;    // Black, White, Navy Blue, Maroon, etc.

    private Double costPrice;   // Cost price
    private Double proPrice;    // Selling price
    private Integer stockQty;   // Current in-stock quantity
    private Integer reorderLevel; // Alert threshold (e.g., 10)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sup_id")
    private SupplierEntity supplier;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderDetailsEntity> orderDetails;
}

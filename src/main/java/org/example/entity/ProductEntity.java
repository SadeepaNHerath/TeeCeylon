package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pro_seq")
    @GenericGenerator(name = "pro_seq", strategy = "org.example.id_generators.ProductIdGenerator"
    )
    private String proId;

    private String proName;
    private String proCategory;

    @ManyToOne
    @JoinColumn(name = "supId")
    private SupplierEntity supplier;

    private String supId;
    private String proSize;
    private Double proPrice;
    private Integer stockQty;

    @OneToMany(mappedBy = "product")
    private List<OrderDetailsEntity> orderDetails;

}

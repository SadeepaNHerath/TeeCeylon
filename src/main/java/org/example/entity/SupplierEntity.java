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
@Table(name = "suppliers")
public class SupplierEntity {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sup_seq")
    @GenericGenerator(name = "sup_seq", strategy = "org.example.id_generators.SupplierIdGenerator"
    )
    private String supId;

    private String supName;
    private String supAddress;
    private String supEmail;
    private String supContact;

    @OneToMany(mappedBy = "supplier")
    private List<ProductEntity> products;
}

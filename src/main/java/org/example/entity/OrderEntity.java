package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@ToString(exclude = {"orderDetails", "customer"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ord_seq")
    @GenericGenerator(name = "ord_seq", strategy = "org.example.id_generators.OrderIdGenerator")
    private String ordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cus_id")
    private CustomerEntity customer;

    private String cusName;
    private String cusPhone;
    private String cusEmail;
    private LocalDate ordDate;
    private LocalTime ordTime;
    private Double ordTotal;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderDetailsEntity> orderDetails;
}

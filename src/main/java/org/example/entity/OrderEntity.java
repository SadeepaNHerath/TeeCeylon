package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ord_seq")
    @GenericGenerator(name = "ord_seq", strategy = "org.example.id_generator.OrderIdGenerator"
    )
    private String ordId;

    private String cusName;
    private String cusEmail;
    private LocalDate ordDate;
    private LocalTime ordTime;
    private Double ordTotal;
}

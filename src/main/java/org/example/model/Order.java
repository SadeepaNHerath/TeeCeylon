package org.example.model;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String ordId;
    private String cusName;
    private String cusEmail;
    private LocalDate ordDate;
    private LocalTime ordTime;
    private Double ordTotal;
}

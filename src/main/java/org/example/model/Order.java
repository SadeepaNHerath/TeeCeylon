package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String ordId;
    private String cusId;
    private String cusName;
    private String cusPhone;
    private String cusEmail;
    private LocalDate ordDate;
    private LocalTime ordTime;
    private Double ordTotal;
}

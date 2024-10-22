package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suppliers")
public class SupplierEntity {
    @Id
    private String supId;

    private String supName;
    private String supAddress;
    private String supEmail;
    private String supContact;
}

package org.example.model;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private String supId;
    private String supName;
    private String supAddress;
    private String supEmail;
    private String supContact;
}

package org.example.model;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private String empId;
    private String empRole;
    private String empName;
    private String contactNum;
    private String address;
    private String email;
    private String password;
}

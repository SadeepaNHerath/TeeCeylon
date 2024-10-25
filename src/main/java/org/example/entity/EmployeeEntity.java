package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_seq")
    @GenericGenerator( name = "emp_seq", strategy = "org.example.id_generators.EmployeeIdGenerator"
    )
    private String empId;

    private String empRole;
    private String empName;
    private String contactNum;
    private String address;
    private String email;
    private String password;
}

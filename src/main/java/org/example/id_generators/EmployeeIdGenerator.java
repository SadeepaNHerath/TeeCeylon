package org.example.id_generators;

import org.example.entity.EmployeeEntity;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class EmployeeIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        try {
            EmployeeEntity employee = (EmployeeEntity) object;
            String role = employee != null && employee.getEmpRole() != null ? employee.getEmpRole().toUpperCase() : "ADMIN";
            String prefix = role.contains("CASHIER") ? "CAS" : "ADM";
            String query = "SELECT COUNT(e) FROM EmployeeEntity e WHERE UPPER(e.empRole) = :role";

            Long count = session.createQuery(query, Long.class)
                    .setParameter("role", role)
                    .getSingleResult();

            return String.format("%s%03d", prefix, (count != null ? count : 0) + 1);
        } catch (Exception e) {
            return "EMP001";
        }
    }
}

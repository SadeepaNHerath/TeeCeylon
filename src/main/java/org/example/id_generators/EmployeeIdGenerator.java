package org.example.id_generators;

import org.example.entity.EmployeeEntity;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class EmployeeIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String prefix = ((EmployeeEntity) object).getEmpRole().equals("CASHIER") ? "CAS" : "ADM";
        String query = "SELECT COUNT(e) FROM EmployeeEntity e WHERE e.empRole = :role";

        Long count = session.createQuery(query, Long.class)
                .setParameter("role", ((EmployeeEntity) object).getEmpRole())
                .getSingleResult();

        return String.format("%s%03d", prefix, count + 1);
    }
}

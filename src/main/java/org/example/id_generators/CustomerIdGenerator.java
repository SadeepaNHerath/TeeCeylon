package org.example.id_generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class CustomerIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        try {
            String query = "SELECT COUNT(c) FROM CustomerEntity c";
            Long count = session.createQuery(query, Long.class).getSingleResult();
            return String.format("CUS%04d", (count != null ? count : 0) + 1);
        } catch (Exception e) {
            return "CUS0001";
        }
    }
}

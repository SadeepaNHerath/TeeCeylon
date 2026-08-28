package org.example.id_generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class ProductIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        try {
            String query = "SELECT COUNT(p) FROM ProductEntity p";
            Long count = session.createQuery(query, Long.class).getSingleResult();
            return String.format("PRO%03d", (count != null ? count : 0) + 1);
        } catch (Exception e) {
            return "PRO001";
        }
    }
}

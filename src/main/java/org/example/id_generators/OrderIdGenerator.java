package org.example.id_generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class OrderIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String query = "SELECT COUNT(o) FROM OrderEntity o";
        Long count = session.createQuery(query, Long.class).getSingleResult();
        return String.format("ORD%04d", count + 1);
    }
}

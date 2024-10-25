package org.example.id_generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class SupplierIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String query = "SELECT COUNT(s) FROM SupplierEntity s";
        Long count = session.createQuery(query, Long.class).getSingleResult();
        return String.format("SUP%03d", count + 1);
    }
}

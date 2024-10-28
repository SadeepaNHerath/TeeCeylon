package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.SupplierEntity;
import org.example.repository.custom.SupplierRepository;
import org.example.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

public class SupplierRepositoryImpl implements SupplierRepository {
    @Override
    public boolean save(SupplierEntity supplierEntity) {
        try {
            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            session.persist(supplierEntity);
            session.getTransaction().commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public boolean update(SupplierEntity supplierEntity) {
        try {
            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            session.merge(supplierEntity);
            session.getTransaction().commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            session.remove(session.get(SupplierEntity.class, id));
            session.getTransaction().commit();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public SupplierEntity searchById(String id) {
        try {
            Session session = HibernateUtil.getSession();
            return session.get(SupplierEntity.class, id);
        } catch (HibernateException e) {
            return null;
        }
    }

    @Override
    public ObservableList<SupplierEntity> getAll() {
        ObservableList<SupplierEntity> supplier = FXCollections.observableArrayList();
        try {
            Session session = HibernateUtil.getSession();
            List<SupplierEntity> supplierEntityList = session.createQuery("From SupplierEntity", SupplierEntity.class).list();
            supplier.addAll(supplierEntityList);
            return supplier;
        } catch (Exception e) {
            return supplier;
        }
    }
}

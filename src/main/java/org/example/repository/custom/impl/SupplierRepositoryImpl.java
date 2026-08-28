package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.SupplierEntity;
import org.example.repository.custom.SupplierRepository;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SupplierRepositoryImpl implements SupplierRepository {
    @Override
    public boolean save(SupplierEntity supplierEntity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            session.persist(supplierEntity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    @Override
    public boolean update(SupplierEntity supplierEntity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            session.merge(supplierEntity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            SupplierEntity entity = session.get(SupplierEntity.class, id);
            if (entity != null) {
                session.remove(entity);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    @Override
    public SupplierEntity searchById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return null;
            return session.get(SupplierEntity.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ObservableList<SupplierEntity> getAll() {
        ObservableList<SupplierEntity> list = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return list;
            List<SupplierEntity> results = session.createQuery("FROM SupplierEntity ORDER BY supId ASC", SupplierEntity.class).list();
            list.addAll(results);
            return list;
        } catch (Exception e) {
            return list;
        }
    }
}

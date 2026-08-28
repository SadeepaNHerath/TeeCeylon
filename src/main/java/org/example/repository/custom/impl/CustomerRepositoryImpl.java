package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.CustomerEntity;
import org.example.repository.custom.CustomerRepository;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CustomerRepositoryImpl implements CustomerRepository {
    @Override
    public boolean save(CustomerEntity customerEntity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            session.persist(customerEntity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    @Override
    public boolean update(CustomerEntity customerEntity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            session.merge(customerEntity);
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
            CustomerEntity customer = session.get(CustomerEntity.class, id);
            if (customer != null) {
                session.remove(customer);
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
    public CustomerEntity searchById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return null;
            return session.get(CustomerEntity.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CustomerEntity searchByPhone(String phone) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null || phone == null || phone.trim().isEmpty()) return null;
            return session.createQuery("FROM CustomerEntity WHERE phone = :phone", CustomerEntity.class)
                    .setParameter("phone", phone.trim())
                    .uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ObservableList<CustomerEntity> getAll() {
        ObservableList<CustomerEntity> list = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return list;
            List<CustomerEntity> results = session.createQuery("FROM CustomerEntity ORDER BY cusId ASC", CustomerEntity.class).list();
            list.addAll(results);
            return list;
        } catch (Exception e) {
            return list;
        }
    }
}

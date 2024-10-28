package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.repository.custom.ProductRepository;
import org.example.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public boolean save(ProductEntity productEntity) {
        try {
            Session session = HibernateUtil.getSession();

            session.beginTransaction();
            session.persist(productEntity);
            session.getTransaction().commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public boolean update(ProductEntity productEntity) {
        try {
            Session session = HibernateUtil.getSession();

            session.beginTransaction();
            session.merge(productEntity);
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
            session.remove(session.get(ProductEntity.class, id));
            session.getTransaction().commit();
            return true;
        } catch (HibernateException e) {
            return false;
        }

    }

    @Override
    public ProductEntity searchById(String id) {
        Session session = HibernateUtil.getSession();

        return session.get(ProductEntity.class, id);
    }

    @Override
    public ObservableList<ProductEntity> getAll() {
        ObservableList<ProductEntity> productList = FXCollections.observableArrayList();
        try {
            Session session = HibernateUtil.getSession();

            List<ProductEntity> productEntityList = session.createQuery("From ProductEntity ", ProductEntity.class).list();
            productList.addAll(productEntityList);
            return productList;
        } catch (Exception e) {
            return productList;
        }
    }

    @Override
    public ObservableList<ProductEntity> getAllProductForSupplier(String id) {
        String hql = "From ProductEntity Where productSupplierId = :supplierId ";
        ObservableList<ProductEntity> productList = FXCollections.observableArrayList();
        try {
            Session session = HibernateUtil.getSession();

            List<ProductEntity> productEntityList = session.createQuery(hql, ProductEntity.class).setParameter("supplierId", id).list();
            productList.addAll(productEntityList); return productList;
        } catch (Exception e) {
            return productList;
        }


    }
}

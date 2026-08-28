package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.repository.custom.ProductRepository;
import org.example.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public boolean save(ProductEntity productEntity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            session.persist(productEntity);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }

    @Override
    public boolean update(ProductEntity productEntity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            session.merge(productEntity);
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
            ProductEntity entity = session.get(ProductEntity.class, id);
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
    public ProductEntity searchById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return null;
            return session.get(ProductEntity.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ProductEntity searchBySku(String sku) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return null;
            return session.createQuery("FROM ProductEntity WHERE sku = :sku", ProductEntity.class)
                    .setParameter("sku", sku)
                    .uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ObservableList<ProductEntity> getAll() {
        ObservableList<ProductEntity> productList = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return productList;
            List<ProductEntity> list = session.createQuery("FROM ProductEntity ORDER BY proId ASC", ProductEntity.class).list();
            productList.addAll(list);
            return productList;
        } catch (Exception e) {
            return productList;
        }
    }

    @Override
    public ObservableList<ProductEntity> getAllProductForSupplier(String id) {
        ObservableList<ProductEntity> productList = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return productList;
            String hql = "FROM ProductEntity WHERE supplier.supId = :supplierId";
            List<ProductEntity> list = session.createQuery(hql, ProductEntity.class)
                    .setParameter("supplierId", id)
                    .list();
            productList.addAll(list);
            return productList;
        } catch (Exception e) {
            return productList;
        }
    }

    @Override
    public ObservableList<ProductEntity> getLowStockProducts() {
        ObservableList<ProductEntity> productList = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return productList;
            String hql = "FROM ProductEntity WHERE stockQty <= reorderLevel OR stockQty <= 10";
            List<ProductEntity> list = session.createQuery(hql, ProductEntity.class).list();
            productList.addAll(list);
            return productList;
        } catch (Exception e) {
            return productList;
        }
    }
}

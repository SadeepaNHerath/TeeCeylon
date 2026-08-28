package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.ProductEntity;
import org.example.entity.PurchaseDetailsEntity;
import org.example.entity.PurchaseEntity;
import org.example.repository.custom.PurchaseRepository;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class PurchaseRepositoryImpl implements PurchaseRepository {

    @Override
    public boolean savePurchase(PurchaseEntity purchase, List<PurchaseDetailsEntity> details) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();

            if (purchase.getPurchaseDate() == null) {
                purchase.setPurchaseDate(LocalDate.now());
            }

            session.persist(purchase);

            for (PurchaseDetailsEntity item : details) {
                item.setPurchaseId(purchase.getPurchaseId());

                ProductEntity product = session.get(ProductEntity.class, item.getProId());
                if (product != null) {
                    int addQty = item.getQty() != null ? item.getQty() : 0;
                    int currentStock = product.getStockQty() != null ? product.getStockQty() : 0;
                    product.setStockQty(currentStock + addQty);

                    if (item.getUnitCost() != null && item.getUnitCost() > 0) {
                        product.setCostPrice(item.getUnitCost());
                    }

                    session.merge(product);
                    item.setProduct(product);
                }

                session.persist(item);
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean save(PurchaseEntity entity) {
        return false;
    }

    @Override
    public boolean update(PurchaseEntity entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            session.merge(entity);
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
            PurchaseEntity purchase = session.get(PurchaseEntity.class, id);
            if (purchase != null) {
                if (purchase.getPurchaseDetails() != null) {
                    for (PurchaseDetailsEntity item : purchase.getPurchaseDetails()) {
                        ProductEntity product = session.get(ProductEntity.class, item.getProId());
                        if (product != null) {
                            int qty = item.getQty() != null ? item.getQty() : 0;
                            product.setStockQty(Math.max(0, (product.getStockQty() != null ? product.getStockQty() : 0) - qty));
                            session.merge(product);
                        }
                    }
                }
                session.remove(purchase);
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
    public PurchaseEntity searchById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return null;
            PurchaseEntity entity = session.get(PurchaseEntity.class, id);
            if (entity != null && entity.getPurchaseDetails() != null) {
                entity.getPurchaseDetails().size();
            }
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ObservableList<PurchaseEntity> getAll() {
        ObservableList<PurchaseEntity> list = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return list;
            List<PurchaseEntity> results = session.createQuery("FROM PurchaseEntity ORDER BY purchaseDate DESC", PurchaseEntity.class).list();
            for (PurchaseEntity p : results) {
                if (p.getPurchaseDetails() != null) p.getPurchaseDetails().size();
            }
            list.addAll(results);
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    @Override
    public ObservableList<PurchaseEntity> getPurchasesByMonth(int month, int year) {
        ObservableList<PurchaseEntity> list = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return list;
            List<PurchaseEntity> results = session.createQuery("FROM PurchaseEntity WHERE MONTH(purchaseDate) = :m AND YEAR(purchaseDate) = :y ORDER BY purchaseDate DESC", PurchaseEntity.class)
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .list();
            for (PurchaseEntity p : results) {
                if (p.getPurchaseDetails() != null) p.getPurchaseDetails().size();
            }
            list.addAll(results);
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    @Override
    public Double getMonthlyPurchasesTotal(int month, int year) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return 0.0;
            Double total = session.createQuery("SELECT SUM(p.totalAmount) FROM PurchaseEntity p WHERE MONTH(p.purchaseDate) = :m AND YEAR(p.purchaseDate) = :y", Double.class)
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .uniqueResult();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public Long getMonthlyItemsBoughtCount(int month, int year) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return 0L;
            Long count = session.createQuery("SELECT SUM(d.qty) FROM PurchaseDetailsEntity d WHERE MONTH(d.purchase.purchaseDate) = :m AND YEAR(d.purchase.purchaseDate) = :y", Long.class)
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .uniqueResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Long getTotalItemsBought() {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return 0L;
            Long count = session.createQuery("SELECT SUM(d.qty) FROM PurchaseDetailsEntity d", Long.class).uniqueResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Double getTotalPurchasesAmount() {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return 0.0;
            Double total = session.createQuery("SELECT SUM(p.totalAmount) FROM PurchaseEntity p", Double.class).uniqueResult();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}

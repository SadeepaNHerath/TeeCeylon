package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.entity.ProductEntity;
import org.example.repository.custom.OrderRepository;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public boolean save(OrderEntity orderEntity, ObservableList<OrderDetailsEntity> orderDetailsList) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();

            if (orderEntity.getOrdDate() == null) {
                orderEntity.setOrdDate(LocalDate.now());
            }
            if (orderEntity.getOrdTime() == null) {
                orderEntity.setOrdTime(LocalTime.now());
            }

            session.persist(orderEntity);

            for (OrderDetailsEntity detail : orderDetailsList) {
                detail.setOrdId(orderEntity.getOrdId());

                ProductEntity product = session.get(ProductEntity.class, detail.getProId());
                if (product == null) {
                    throw new RuntimeException("Product not found: " + detail.getProId());
                }

                int reqQty = detail.getProQty() != null ? detail.getProQty() : 1;
                int currentStock = product.getStockQty() != null ? product.getStockQty() : 0;

                if (currentStock < reqQty) {
                    throw new RuntimeException("Insufficient stock for product " + product.getProName() + " (Available: " + currentStock + ", Requested: " + reqQty + ")");
                }

                product.setStockQty(currentStock - reqQty);
                session.merge(product);

                detail.setProduct(product);
                session.persist(detail);
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
    public boolean update(OrderEntity orderEntity, ObservableList<OrderDetailsEntity> newDetailEntities) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();

            OrderEntity existingOrder = session.get(OrderEntity.class, orderEntity.getOrdId());
            if (existingOrder == null) return false;

            existingOrder.setCusName(orderEntity.getCusName());
            existingOrder.setCusPhone(orderEntity.getCusPhone());
            existingOrder.setCusEmail(orderEntity.getCusEmail());
            existingOrder.setOrdTotal(orderEntity.getOrdTotal());

            // Adjust stock for existing items
            if (existingOrder.getOrderDetails() != null) {
                for (OrderDetailsEntity oldDetail : existingOrder.getOrderDetails()) {
                    ProductEntity product = session.get(ProductEntity.class, oldDetail.getProId());
                    if (product != null) {
                        product.setStockQty(product.getStockQty() + oldDetail.getProQty());
                        session.merge(product);
                    }
                    session.remove(oldDetail);
                }
                existingOrder.getOrderDetails().clear();
            }

            // Add new items and decrement stock
            for (OrderDetailsEntity newDetail : newDetailEntities) {
                newDetail.setOrdId(existingOrder.getOrdId());
                ProductEntity product = session.get(ProductEntity.class, newDetail.getProId());
                if (product == null || product.getStockQty() < newDetail.getProQty()) {
                    throw new RuntimeException("Insufficient stock level for product: " + (product != null ? product.getProName() : newDetail.getProId()));
                }
                product.setStockQty(product.getStockQty() - newDetail.getProQty());
                session.merge(product);

                newDetail.setProduct(product);
                session.persist(newDetail);
            }

            session.merge(existingOrder);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return false;
            tx = session.beginTransaction();
            OrderEntity order = session.get(OrderEntity.class, id);
            if (order != null) {
                if (order.getOrderDetails() != null) {
                    for (OrderDetailsEntity detail : order.getOrderDetails()) {
                        ProductEntity product = session.get(ProductEntity.class, detail.getProId());
                        if (product != null) {
                            product.setStockQty(product.getStockQty() + detail.getProQty());
                            session.merge(product);
                        }
                    }
                }
                session.remove(order);
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
    public OrderEntity searchById(String id) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return null;
            OrderEntity order = session.get(OrderEntity.class, id);
            if (order != null && order.getOrderDetails() != null) {
                order.getOrderDetails().size(); // force initialize
            }
            return order;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ObservableList<OrderEntity> getAll() {
        ObservableList<OrderEntity> list = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return list;
            List<OrderEntity> results = session.createQuery("FROM OrderEntity ORDER BY ordDate DESC, ordTime DESC", OrderEntity.class).list();
            for (OrderEntity o : results) {
                if (o.getOrderDetails() != null) o.getOrderDetails().size();
            }
            list.addAll(results);
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    @Override
    public boolean save(OrderEntity orderEntity) {
        return false;
    }

    @Override
    public boolean update(OrderEntity orderEntity) {
        return false;
    }

    @Override
    public ObservableList<OrderEntity> getOrdersByDate(LocalDate date) {
        ObservableList<OrderEntity> list = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return list;
            List<OrderEntity> results = session.createQuery("FROM OrderEntity WHERE ordDate = :date ORDER BY ordTime DESC", OrderEntity.class)
                    .setParameter("date", date)
                    .list();
            for (OrderEntity o : results) {
                if (o.getOrderDetails() != null) o.getOrderDetails().size();
            }
            list.addAll(results);
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    @Override
    public ObservableList<OrderEntity> getOrdersByMonth(int month, int year) {
        ObservableList<OrderEntity> list = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return list;
            List<OrderEntity> results = session.createQuery("FROM OrderEntity WHERE MONTH(ordDate) = :m AND YEAR(ordDate) = :y ORDER BY ordDate DESC", OrderEntity.class)
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .list();
            for (OrderEntity o : results) {
                if (o.getOrderDetails() != null) o.getOrderDetails().size();
            }
            list.addAll(results);
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    @Override
    public ObservableList<OrderEntity> getOrdersByYear(int year) {
        ObservableList<OrderEntity> list = FXCollections.observableArrayList();
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return list;
            List<OrderEntity> results = session.createQuery("FROM OrderEntity WHERE YEAR(ordDate) = :y ORDER BY ordDate DESC", OrderEntity.class)
                    .setParameter("y", year)
                    .list();
            for (OrderEntity o : results) {
                if (o.getOrderDetails() != null) o.getOrderDetails().size();
            }
            list.addAll(results);
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    @Override
    public Long getMonthlySalesCount(int month, int year) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return 0L;
            Long count = session.createQuery("SELECT COUNT(o) FROM OrderEntity o WHERE MONTH(o.ordDate) = :m AND YEAR(o.ordDate) = :y", Long.class)
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .uniqueResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public Double getMonthlySalesTotal(int month, int year) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return 0.0;
            Double total = session.createQuery("SELECT SUM(o.ordTotal) FROM OrderEntity o WHERE MONTH(o.ordDate) = :m AND YEAR(o.ordDate) = :y", Double.class)
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .uniqueResult();
            return total != null ? total : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public Long getMonthlyItemsSoldCount(int month, int year) {
        try (Session session = HibernateUtil.getSession()) {
            if (session == null) return 0L;
            Long count = session.createQuery("SELECT SUM(d.proQty) FROM OrderDetailsEntity d WHERE MONTH(d.order.ordDate) = :m AND YEAR(d.order.ordDate) = :y", Long.class)
                    .setParameter("m", month)
                    .setParameter("y", year)
                    .uniqueResult();
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}

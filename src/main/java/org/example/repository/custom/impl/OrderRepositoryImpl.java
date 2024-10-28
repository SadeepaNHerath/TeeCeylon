package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.entity.ProductEntity;
import org.example.repository.custom.OrderRepository;
import org.example.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {
    @Override
    public boolean update(OrderEntity orderEntity, ObservableList<OrderDetailsEntity> orderDetailEntities) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            OrderEntity existingOrder = session.get(OrderEntity.class, orderEntity.getOrdId());
            if (existingOrder == null) {
                return false;
            }

            existingOrder.setCusName(orderEntity.getCusName());
            existingOrder.setCusEmail(orderEntity.getCusEmail());
            existingOrder.setOrdTotal(orderEntity.getOrdTotal());

            for (OrderDetailsEntity newDetail : orderDetailEntities) {
                boolean found = false;
                for (OrderDetailsEntity existingDetail : existingOrder.getOrderDetails()) {
                    if (existingDetail.getProId().equals(newDetail.getProId())) {
                        found = true;
                        int qtyDifference = existingDetail.getProQty() - newDetail.getProQty();

                        existingDetail.setProQty(newDetail.getProQty());
                        existingDetail.setProTotal(newDetail.getProTotal());

                        ProductEntity product = session.get(ProductEntity.class, newDetail.getProId());
                        if (product.getStockQty() + qtyDifference >= 0) {
                            product.setStockQty(product.getStockQty() + qtyDifference);
                        } else {
                            throw new RuntimeException("Insufficient stock level for product: ");
                        }

                        session.merge(product);
                        session.merge(existingDetail);
                    }
                }
                if (!found) {
                    existingOrder.getOrderDetails().add(newDetail);

                    ProductEntity product = session.get(ProductEntity.class, newDetail.getProId());
                    if (product.getStockQty() >= newDetail.getProQty()) {
                        product.setStockQty(product.getStockQty() - newDetail.getProQty());
                    } else {
                        throw new RuntimeException(STR."Insufficient stock level for new product: \{product.getProName()}");
                    }

                    session.merge(product);
                    session.persist(newDetail);
                }
            }
            List<OrderDetailsEntity> detailsToRemove = new ArrayList<>();
            for (OrderDetailsEntity existingDetail : existingOrder.getOrderDetails()) {
                boolean isStillPresent = false;
                for (OrderDetailsEntity newDetail : orderDetailEntities) {
                    if (newDetail.getProId().equals(existingDetail.getProId())) {
                        isStillPresent = true;
                        break;
                    }
                }
                if (!isStillPresent) {
                    detailsToRemove.add(existingDetail);
                    ProductEntity product = session.get(ProductEntity.class, existingDetail.getProId());
                    product.setStockQty(product.getStockQty() + existingDetail.getProQty());
                    session.merge(product);
                }
            }

            for (OrderDetailsEntity detailToRemove : detailsToRemove) {
                session.remove(detailToRemove);
            }
            existingOrder.getOrderDetails().removeAll(detailsToRemove);
            session.merge(existingOrder);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean save(OrderEntity orderEntity, ObservableList<OrderDetailsEntity> orderDetailsEntity
) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.persist(orderEntity);

            for (OrderDetailsEntity orderDetail : orderDetailsEntity
) {
                session.persist(orderDetail);

                ProductEntity product = session.get(ProductEntity.class, orderDetail.getProId());
                if (product.getStockQty() > orderDetail.getProQty()) {
                    product.setStockQty(product.getStockQty() - orderDetail.getProQty());
                } else {
                    throw new RuntimeException("Insufficient stock level");
                }
            }

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean save(OrderEntity orderEntity) {
        try {
            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            session.persist(orderEntity);
            session.getTransaction().commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(OrderEntity orderEntity) {
        return false;
    }

    @Override
    public boolean delete(String id) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            OrderEntity orderEntity = session.get(OrderEntity.class, id);

            if (orderEntity == null) {
                return false;
            }

            List<OrderDetailsEntity> orderDetails = orderEntity.getOrderDetails();
            for (OrderDetailsEntity detail : orderDetails) {
                ProductEntity product = session.get(ProductEntity.class, detail.getProId());
                if (product != null) {
                    product.setStockQty(product.getStockQty() + detail.getProQty());
                    session.merge(product);
                }
            }

            session.remove(orderEntity);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public OrderEntity searchById(String id) {
        try {
            Session session = HibernateUtil.getSession();
            return session.get(OrderEntity.class, id);
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public ObservableList<OrderEntity> getAll() {
        ObservableList<OrderEntity> orderList = FXCollections.observableArrayList();
        try {
            Session session = HibernateUtil.getSession();
            List<OrderEntity> orderEntityList = session.createQuery("From OrderEntity", OrderEntity.class).list();
            orderList.addAll(orderEntityList);
            return orderList;
        } catch (Exception e) {
            e.printStackTrace();
            return orderList;
        }
    }
}

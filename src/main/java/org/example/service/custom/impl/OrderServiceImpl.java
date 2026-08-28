package org.example.service.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.CustomerEntity;
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.entity.ProductEntity;
import org.example.model.Order;
import org.example.model.OrderDetails;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.CustomerRepository;
import org.example.repository.custom.OrderRepository;
import org.example.repository.custom.ProductRepository;
import org.example.service.custom.OrderService;
import org.example.util.RepositoryType;

import java.time.LocalDate;
import java.time.LocalTime;

public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.ORDER);
    private final CustomerRepository customerRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.CUSTOMER);
    private final ProductRepository productRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.PRODUCT);

    @Override
    public ObservableList<OrderEntity> getAllOrders() {
        return orderRepository.getAll();
    }

    @Override
    public Boolean addOrder(Order order, ObservableList<OrderDetails> orderDetails) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrdId(order.getOrdId());
        orderEntity.setCusName(order.getCusName());
        orderEntity.setCusPhone(order.getCusPhone());
        orderEntity.setCusEmail(order.getCusEmail());
        orderEntity.setOrdDate(order.getOrdDate() != null ? order.getOrdDate() : LocalDate.now());
        orderEntity.setOrdTime(order.getOrdTime() != null ? order.getOrdTime() : LocalTime.now());
        orderEntity.setOrdTotal(order.getOrdTotal());

        if (order.getCusPhone() != null && !order.getCusPhone().trim().isEmpty()) {
            CustomerEntity customer = customerRepository.searchByPhone(order.getCusPhone().trim());
            orderEntity.setCustomer(customer);
        }

        ObservableList<OrderDetailsEntity> orderDetailEntities = FXCollections.observableArrayList();
        for (OrderDetails detail : orderDetails) {
            OrderDetailsEntity entity = new OrderDetailsEntity();
            entity.setOrdId(order.getOrdId());
            entity.setProId(detail.getProId());
            entity.setProQty(detail.getProQty());
            entity.setUnitPrice(detail.getUnitPrice());
            entity.setProTotal(detail.getProTotal());

            ProductEntity product = productRepository.searchById(detail.getProId());
            entity.setProduct(product);

            orderDetailEntities.add(entity);
        }

        return orderRepository.save(orderEntity, orderDetailEntities);
    }

    @Override
    public OrderEntity searchOrderById(String id) {
        return orderRepository.searchById(id);
    }

    @Override
    public boolean updateOrder(Order order, ObservableList<OrderDetails> orderDetails) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrdId(order.getOrdId());
        orderEntity.setCusName(order.getCusName());
        orderEntity.setCusPhone(order.getCusPhone());
        orderEntity.setCusEmail(order.getCusEmail());
        orderEntity.setOrdTotal(order.getOrdTotal());

        ObservableList<OrderDetailsEntity> orderDetailEntities = FXCollections.observableArrayList();
        for (OrderDetails detail : orderDetails) {
            OrderDetailsEntity entity = new OrderDetailsEntity();
            entity.setOrdId(order.getOrdId());
            entity.setProId(detail.getProId());
            entity.setProQty(detail.getProQty());
            entity.setUnitPrice(detail.getUnitPrice());
            entity.setProTotal(detail.getProTotal());

            ProductEntity product = productRepository.searchById(detail.getProId());
            entity.setProduct(product);

            orderDetailEntities.add(entity);
        }

        return orderRepository.update(orderEntity, orderDetailEntities);
    }

    @Override
    public boolean deleteOrder(String id) {
        return orderRepository.delete(id);
    }

    @Override
    public ObservableList<OrderEntity> getOrdersByDate(LocalDate date) {
        return orderRepository.getOrdersByDate(date);
    }

    @Override
    public ObservableList<OrderEntity> getOrdersByMonth(int month, int year) {
        return orderRepository.getOrdersByMonth(month, year);
    }

    @Override
    public ObservableList<OrderEntity> getOrdersByYear(int year) {
        return orderRepository.getOrdersByYear(year);
    }

    @Override
    public Long getMonthlySalesCount(int month, int year) {
        return orderRepository.getMonthlySalesCount(month, year);
    }

    @Override
    public Double getMonthlySalesTotal(int month, int year) {
        return orderRepository.getMonthlySalesTotal(month, year);
    }

    @Override
    public Long getMonthlyItemsSoldCount(int month, int year) {
        return orderRepository.getMonthlyItemsSoldCount(month, year);
    }

    @Override
    public String generateNextOrderId() {
        ObservableList<OrderEntity> all = orderRepository.getAll();
        int max = 0;
        for (OrderEntity o : all) {
            if (o.getOrdId() != null && o.getOrdId().startsWith("ORD")) {
                try {
                    int num = Integer.parseInt(o.getOrdId().substring(3));
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }
        }
        return String.format("ORD%04d", max + 1);
    }
}

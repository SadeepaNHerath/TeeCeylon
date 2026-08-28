package org.example.service.custom;

import javafx.collections.ObservableList;
import org.example.entity.OrderEntity;
import org.example.model.Order;
import org.example.model.OrderDetails;
import org.example.service.SuperService;

import java.time.LocalDate;

public interface OrderService extends SuperService {
    ObservableList<OrderEntity> getAllOrders();

    Boolean addOrder(Order order, ObservableList<OrderDetails> orderDetails);

    OrderEntity searchOrderById(String id);

    boolean updateOrder(Order order, ObservableList<OrderDetails> orderDetails);

    boolean deleteOrder(String id);

    ObservableList<OrderEntity> getOrdersByDate(LocalDate date);

    ObservableList<OrderEntity> getOrdersByMonth(int month, int year);

    ObservableList<OrderEntity> getOrdersByYear(int year);

    Long getMonthlySalesCount(int month, int year);

    Double getMonthlySalesTotal(int month, int year);

    Long getMonthlyItemsSoldCount(int month, int year);

    String generateNextOrderId();
}

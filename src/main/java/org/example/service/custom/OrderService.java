package org.example.service.custom;

import javafx.collections.ObservableList;
import org.example.entity.OrderEntity;
import org.example.model.Order;
import org.example.model.OrderDetails;
import org.example.service.SuperService;

public interface OrderService extends SuperService{
    ObservableList<OrderEntity> getAllOrders();

    Boolean addOrder(Order order, ObservableList<OrderDetails> orderDetails);

    OrderEntity searchOrderById(String id);

    boolean updateOrder(Order order,ObservableList<OrderDetails> orderDetails);

    boolean deleteOrder(String id);
}

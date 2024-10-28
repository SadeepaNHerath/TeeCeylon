package org.example.service.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.model.Order;
import org.example.model.OrderDetails;
import org.example.repository.RepositoryFactory;
import org.example.repository.custom.OrderRepository;
import org.example.service.custom.OrderService;
import org.example.util.RepositoryType;
import org.modelmapper.ModelMapper;

public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository = RepositoryFactory.getInstance().getRepository(RepositoryType.ORDER);

    @Override
    public ObservableList<OrderEntity> getAllOrders() {
        return orderRepository.getAll();
    }

    @Override
    public Boolean addOrder(Order order, ObservableList<OrderDetails> orderDetails) {
        ObservableList<OrderDetailsEntity> orderDetailEntities = FXCollections.observableArrayList();
        orderDetails.forEach(orderDetail -> orderDetailEntities.add(new ModelMapper().map(orderDetail, OrderDetailsEntity.class)));
        OrderEntity orderEntity = new ModelMapper().map(order, OrderEntity.class);
        orderEntity.setOrderDetails(orderDetailEntities);
        return orderRepository.save(orderEntity, orderDetailEntities);
    }

    @Override
    public OrderEntity searchOrderById(String id) {
        return orderRepository.searchById(id);
    }

    @Override
    public boolean updateOrder(Order order, ObservableList<OrderDetails> orderDetails) {
        ObservableList<OrderDetailsEntity> orderDetailEntities = FXCollections.observableArrayList();
        orderDetails.forEach(orderDetail -> orderDetailEntities.add(new ModelMapper().map(orderDetail, OrderDetailsEntity.class)));
        OrderEntity orderEntity = new ModelMapper().map(order, OrderEntity.class);
        orderEntity.setOrderDetails(orderDetailEntities);
        return orderRepository.update(orderEntity, orderDetailEntities);
    }

    @Override
    public boolean deleteOrder(String id) {
        return orderRepository.delete(id);
    }
}

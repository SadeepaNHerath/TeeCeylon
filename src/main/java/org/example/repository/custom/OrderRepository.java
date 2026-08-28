package org.example.repository.custom;

import javafx.collections.ObservableList;
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends CrudRepository<OrderEntity> {
    boolean save(OrderEntity orderEntity, ObservableList<OrderDetailsEntity> orderDetailsEntity);
    boolean update(OrderEntity orderEntity, ObservableList<OrderDetailsEntity> orderDetailsEntity);
    ObservableList<OrderEntity> getOrdersByDate(LocalDate date);
    ObservableList<OrderEntity> getOrdersByMonth(int month, int year);
    ObservableList<OrderEntity> getOrdersByYear(int year);
    Long getMonthlySalesCount(int month, int year);
    Double getMonthlySalesTotal(int month, int year);
    Long getMonthlyItemsSoldCount(int month, int year);
}

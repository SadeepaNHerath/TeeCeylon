package org.example.repository.custom;

import javafx.collections.ObservableList;
import org.example.entity.OrderDetailsEntity;
import org.example.entity.OrderEntity;
import org.example.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderEntity> {
    boolean save(OrderEntity orderEntity, ObservableList<OrderDetailsEntity> orderDetailsEntity
);
    boolean update(OrderEntity orderEntity,ObservableList<OrderDetailsEntity> orderDetailsEntity
);

}

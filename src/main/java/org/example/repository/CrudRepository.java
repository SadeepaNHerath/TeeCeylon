package org.example.repository;

import javafx.collections.ObservableList;

public interface CrudRepository<T> extends SuperRepository {
    boolean save(T t);

    boolean delete(String id);

    boolean update(T t);

    ObservableList<T> getAll();

    T searchById(String id);
}

package org.example.repository.custom.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.EmployeeEntity;
import org.example.repository.custom.EmployeeRepository;
import org.example.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    @Override
    public boolean save(EmployeeEntity employee) {
        try {
            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            session.persist(employee);
            session.getTransaction().commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public boolean update(EmployeeEntity employee) {
        try {
            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            session.merge(employee.getEmpId(), employee);
            session.getTransaction().commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            Session session = HibernateUtil.getSession();
            session.beginTransaction();
            session.remove(session.get(EmployeeEntity.class, id));
            session.getTransaction().commit();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    @Override
    public EmployeeEntity searchById(String id) {
        try {
            Session session = HibernateUtil.getSession();
            return session.get(EmployeeEntity.class, id);
        } catch (HibernateException e) {
            return null;
        }
    }

    @Override
    public ObservableList<EmployeeEntity> getAll() {
        ObservableList<EmployeeEntity> employee = FXCollections.observableArrayList();
        try {
            Session session = HibernateUtil.getSession();
            Query<EmployeeEntity> query = session.createQuery("FROM EmployeeEntity", EmployeeEntity.class);
            List<EmployeeEntity> employeeEntityList = query.list();
            employee.addAll(employeeEntityList);
            return employee;
        } catch (HibernateException e) {
            return employee;
        }
    }
}

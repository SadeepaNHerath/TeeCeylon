package org.example.controller.model_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.entity.OrderEntity;
import org.example.model.Order;
import org.example.model.OrderDetails;
import org.example.service.ServiceFactory;
import org.example.service.custom.OrderService;
import org.example.util.ServiceType;

import java.time.LocalDate;
import java.time.Month;

public class OrderController {
    private static OrderController instance;
    private OrderController(){}

    public static OrderController getInstance() {
        return instance==null?instance=new OrderController():instance;
    }

    OrderService orderService= ServiceFactory.getInstance().getService(ServiceType.ORDER);

    public Boolean placeOrder(Order order, ObservableList<OrderDetails> orderDetails){
        return orderService.addOrder(order,orderDetails);
    }
    public OrderEntity searchOrderById(String id){
        return orderService.searchOrderById(id);
    }

    public ObservableList<OrderEntity> getAllOrders() {
        return orderService.getAllOrders();
    }

    public boolean updateOrder(Order order, ObservableList<OrderDetails> orderDetailsList) {
        return orderService.updateOrder(order,orderDetailsList);
    }

    public Boolean deleteOrder(String id){
        return orderService.deleteOrder(id);
    }

    public Double getDailySalesIncome(LocalDate date){
        ObservableList<OrderEntity> allOrders = getAllOrders();
        double dailySalesIncome = 0.00;
        for(OrderEntity order: allOrders){
            if (order.getOrdDate().equals(date)){
                dailySalesIncome+=order.getOrdTotal();
            }
        }
        return dailySalesIncome;
    }

    public Double getMonthlySalesIncome(Month month){
        ObservableList<OrderEntity> allOrders = getAllOrders();
        double monthlySalesIncome = 0.00;
        for(OrderEntity order: allOrders){
            if (order.getOrdDate().getMonth().equals(month)){
                monthlySalesIncome +=order.getOrdTotal();
            }
        }
        return monthlySalesIncome;
    }

    public Double getAnnualSalesIncome(int year){
        ObservableList<OrderEntity> allOrders = getAllOrders();
        double annualSalesIncome = 0.00;
        for(OrderEntity order: allOrders){
            if (order.getOrdDate().getYear() == year){
                annualSalesIncome +=order.getOrdTotal();
            }
        }
        return annualSalesIncome;
    }

    public Double[] getHourlySalesForTheDay(){
        Double[] hourlySales = new Double[24];

        for (int i = 0; i < hourlySales.length; i++) {
            hourlySales[i] = 0.0;
        }

        ObservableList<OrderEntity> allOrders = getAllOrders();
        for (OrderEntity order : allOrders) {
            if (order.getOrdDate().equals(LocalDate.now())) {
                int orderHour = order.getOrdTime().getHour();
                hourlySales[orderHour] += order.getOrdTotal();
            }
        }

        return hourlySales;
    }

    public ObservableList<OrderEntity> getDailyOrders(LocalDate date){
        ObservableList<OrderEntity> allOrders = getAllOrders();
        ObservableList<OrderEntity> dailySalesOrders= FXCollections.observableArrayList();
        for(OrderEntity order: allOrders){
            if (order.getOrdDate().equals(date)){
                dailySalesOrders.add(order);
            }
        }
        return dailySalesOrders;
    }

    public ObservableList<OrderEntity> getMonthlyOrders(Month month){
        ObservableList<OrderEntity> allOrders = getAllOrders();
        ObservableList<OrderEntity> monthlySalesOrders= FXCollections.observableArrayList();
        for(OrderEntity order: allOrders){
            if (order.getOrdDate().getMonth().equals(month)){
                monthlySalesOrders.add(order);
            }
        }
        return monthlySalesOrders;
    }

    public ObservableList<OrderEntity> getAnnualOrders(int year){
        ObservableList<OrderEntity> allOrders = getAllOrders();
        ObservableList<OrderEntity> annualSalesOrders= FXCollections.observableArrayList();
        for(OrderEntity order: allOrders){
            if (order.getOrdDate().getYear() == year){
                annualSalesOrders.add(order);
            }
        }
        return annualSalesOrders;
    }
}

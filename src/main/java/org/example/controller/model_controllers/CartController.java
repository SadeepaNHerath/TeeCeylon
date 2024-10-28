package org.example.controller.model_controllers;

import javafx.scene.control.Alert;

public class CartController {
    private static CartController instance;
    private  CartController(){}

    public static CartController getInstance() {
        return instance==null?instance=new CartController():instance;
    }

    public Boolean validateProductSelected(String productId){
        if (productId==null){
            new Alert(Alert.AlertType.ERROR,"Select a product").showAndWait();
            return false;
        }else {
            return true;
        }
    }

    public Boolean validateAddingQty(Integer stockQty,Integer addedQty){
        if (stockQty == null || addedQty == null) {
            new Alert(Alert.AlertType.ERROR,"Invalid quantity values").showAndWait();
            return false;
        }

        if (addedQty <= 0) {
            new Alert(Alert.AlertType.ERROR,"Please enter a valid quantity").showAndWait();
            return false;
        }

        if (addedQty > stockQty) {
            new Alert(Alert.AlertType.ERROR,"Insufficient stock available. Current stock: $(stockQty)").showAndWait();
            return false;
        }

        return true;
    }
}

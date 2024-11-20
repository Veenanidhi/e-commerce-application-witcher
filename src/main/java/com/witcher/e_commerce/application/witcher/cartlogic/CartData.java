package com.witcher.e_commerce.application.witcher.cartlogic;

import com.witcher.e_commerce.application.witcher.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class CartData {

    public static List<Product> cart;
    static {
        cart = new ArrayList<Product>();
    }
}

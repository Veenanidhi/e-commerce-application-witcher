package com.witcher.e_commerce.application.witcher.controller.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message) {
        super(message);
    }


}


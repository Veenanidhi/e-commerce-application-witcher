package com.witcher.e_commerce.application.witcher.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ProductOfferDTO {


    private Long productOfferId;
    private String productOfferName;
    private String description;
    private Double discountPercentage;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private Long productId;
    private List<Long> products;


    public void setProductOfferId(Long productOfferId) {
        this.productOfferId = productOfferId;
    }

    public void setProductOfferName(String productOfferName) {
        this.productOfferName = productOfferName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }


    public void setProducts(List<Long> products) {
        this.products = products;
    }

    public void setProductIds(List<Long> productIds) {
    }


    public void setProductId(Long productId) {
        this.productId = productId;
    }





}

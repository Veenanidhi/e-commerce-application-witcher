package com.witcher.e_commerce.application.witcher.dto;

import com.witcher.e_commerce.application.witcher.entity.Category;
import com.witcher.e_commerce.application.witcher.entity.Product;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OfferDTO {

    public String getDiscription;
    private List <Long> categoryIds;
    private Long productOfferId;
    private String productOfferName;
    private String startDate;
    private String expiryDate;

    private String description;

    private String categoryOfferName;

    private Double discountPercentage;

    private boolean isActive ;
    private Set<Product> productList;

    private Long categoryOfferId;

    private Set<Category> categories = new HashSet<>();

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Long getProductOfferId() {
        return productOfferId;
    }

    public void setProductOfferId(Long productOfferId) {
        this.productOfferId = productOfferId;
    }

    public String getProductOfferName() {
        return productOfferName;
    }

    public void setProductOfferName(String productOfferName) {
        this.productOfferName = productOfferName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCategoryOfferName() {
        return categoryOfferName;
    }

    public void setCategoryOfferName(String categoryOfferName) {
        this.categoryOfferName = categoryOfferName;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<Product> getProductList() {
        return productList;
    }

    public void setProductList(Set<Product> productList) {
        this.productList = productList;
    }

    public String getGetDiscription() {
        return getDiscription;
    }

    public void setGetDiscription(String getDiscription) {
        this.getDiscription = getDiscription;
    }

    public String getDescription() {
        return description;
    }

    public Long getCategoryOfferId() {
        return categoryOfferId;
    }

    public void setCategoryOfferId(Long categoryOfferId) {
        this.categoryOfferId = categoryOfferId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }
}

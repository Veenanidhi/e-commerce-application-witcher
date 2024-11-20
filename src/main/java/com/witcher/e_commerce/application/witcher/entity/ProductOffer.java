package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ProductOffer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long productOfferId;

    private String productOfferName;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private Double discountPercentage;

    private boolean isEnabled = true;

    private boolean isActive = true;

    @OneToMany(mappedBy = "productOffer",cascade={CascadeType.PERSIST, CascadeType.MERGE},fetch=FetchType.EAGER)
    private Set<Product> productList = new HashSet<>();

    public ProductOffer() {
    }

    public ProductOffer(Long productOfferId, String productOfferName, String description, LocalDate startDate, LocalDate expiryDate, Double discountPercentage, boolean isEnabled, boolean isActive, Set<Product> productList) {
        this.productOfferId = productOfferId;
        this.productOfferName = productOfferName;
        this.description = description;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.discountPercentage = discountPercentage;
        this.isEnabled = isEnabled;
        this.isActive = isActive;
        this.productList = productList;
    }

    public ProductOffer(Long productOfferId, String productOfferName, LocalDate startDate, LocalDate expiryDate, Double discountPercentage, boolean isEnabled, boolean isActive, Set<Product> productList) {
        this.productOfferId = productOfferId;
        this.productOfferName = productOfferName;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.discountPercentage = discountPercentage;
        this.isEnabled = isEnabled;
        this.isActive = isActive;
        this.productList = productList;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProductList(Set<Product> productList) {
        this.productList = productList;
    }


}

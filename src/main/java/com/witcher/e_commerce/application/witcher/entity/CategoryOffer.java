package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
public class CategoryOffer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long categoryOfferId;

    private String categoryOfferName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private boolean isEnabled =true;

    private boolean isActive=true;

    private String description;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @OneToMany(mappedBy = "categoryOffer")
    private List<Product> products = new ArrayList<>();

    private Double discountPercentage;

    public CategoryOffer() {
    }

    public CategoryOffer(Long categoryOfferId, String categoryOfferName, LocalDate startDate, LocalDate expiryDate, boolean isEnabled, boolean isActive, String description, Category category, List<Product> products, Double discountPercentage) {
        this.categoryOfferId = categoryOfferId;
        this.categoryOfferName = categoryOfferName;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.isEnabled = isEnabled;
        this.isActive = isActive;
        this.description = description;
        this.category = category;
        this.products = products;
        this.discountPercentage = discountPercentage;
    }


    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
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

    public Long getCategoryOfferId() {
        return categoryOfferId;
    }

    public void setCategoryOfferId(Long categoryOfferId) {
        this.categoryOfferId = categoryOfferId;
    }

    public String getCategoryOfferName() {
        return categoryOfferName;
    }

    public void setCategoryOfferName(String categoryOfferName) {
        this.categoryOfferName = categoryOfferName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}

package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Entity
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

    private Double discountPercentage;

    @OneToMany(mappedBy = "categoryOffer", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private Set<Category> categories = new HashSet<>();

    public CategoryOffer() {
    }

    public CategoryOffer(Long categoryOfferId, String categoryOfferName, LocalDate startDate, LocalDate expiryDate, boolean isEnabled, boolean isActive, String description, Double discountPercentage, Set<Category> categories) {
        this.categoryOfferId = categoryOfferId;
        this.categoryOfferName = categoryOfferName;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.isEnabled = isEnabled;
        this.isActive = isActive;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.categories = categories;
    }

    public CategoryOffer(Long categoryOfferId, String categoryOfferName, LocalDate startDate, LocalDate expiryDate, boolean isEnabled, boolean isActive, Double discountPercentage, Set<Category> categories) {
        this.categoryOfferId = categoryOfferId;
        this.categoryOfferName = categoryOfferName;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.isEnabled = isEnabled;
        this.isActive = isActive;
        this.discountPercentage = discountPercentage;
        this.categories = categories;
    }

    public Long getCategoryOfferId() {
        return categoryOfferId;
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.setCategoryOffer(this); // Set the relationship on the owning side
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

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String getDiscription) {
    }

    public void setStartDate(String startDate, DateTimeFormatter formatter) {
    }

    public void setExpiryDate(String expiryDate, DateTimeFormatter formatter) {
    }






}

package com.witcher.e_commerce.application.witcher.dto;

import com.witcher.e_commerce.application.witcher.entity.Category;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public class CategoryOfferDTO {

    private Long categoryOfferId;

    private String categoryOfferName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private String description;

    private Double discountPercentage;

    private Long categoryId;

    private boolean isEnabled;

    private boolean isActive;

    private Set<Category> categories;

    public CategoryOfferDTO() {
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

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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


    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "CategoryOfferDTO{" +
                "name='" + categoryOfferName + '\'' +
                ", startDate=" + startDate +
                ", expiryDate=" + expiryDate +
                ", discount=" + discountPercentage +
                ", categoryId=" + categoryId +
                '}';
    }




}

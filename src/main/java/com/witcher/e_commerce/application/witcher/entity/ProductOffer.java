package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @ManyToMany(mappedBy = "productOffers", fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();


    public ProductOffer() {
    }

    public ProductOffer(Long productOfferId, String productOfferName, String description, LocalDate startDate, LocalDate expiryDate, Double discountPercentage, boolean isEnabled, boolean isActive, List<Product> products) {
        this.productOfferId = productOfferId;
        this.productOfferName = productOfferName;
        this.description = description;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.discountPercentage = discountPercentage;
        this.isEnabled = isEnabled;
        this.isActive = isActive;
        this.products = products;
    }

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return isEnabled && isActive && startDate != null && expiryDate != null &&
                (today.isEqual(startDate) || today.isAfter(startDate)) &&
                (today.isEqual(expiryDate) || today.isBefore(expiryDate));
    }


    public boolean isEnabled() {
        return isEnabled && LocalDate.now().isAfter(startDate) && LocalDate.now().isBefore(expiryDate);
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isActive() {
        return isActive;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}

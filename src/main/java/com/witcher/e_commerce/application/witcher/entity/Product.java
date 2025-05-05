package com.witcher.e_commerce.application.witcher.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;


    @Column(name = "price")
    private double price;

    @Column(name = "stock")
    private String stock;

    private String imageName;

    private String description;

    private String color;

    private String size;

    @ManyToMany(mappedBy = "products")
    private List<Wishlist> wishlists = new ArrayList<>();

    @NotNull
    @Min(0)
    @Max(100)
    private double discountedPrice;

    private boolean isDeleted=false;

    @ManyToMany
    @JoinTable(
            name = "product_product_offer",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "product_offer_id")
    )
    private List<ProductOffer> productOffers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_offer_id", referencedColumnName = "categoryOfferId")
    private CategoryOffer categoryOffer;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductOffer> getProductOffers() {
        return productOffers;
    }

    public void setProductOffers(List<ProductOffer> productOffers) {
        this.productOffers = productOffers;
    }

    public double getDiscountedPrice() {
        if (productOffers != null) {
            for (ProductOffer offer : productOffers) {
                if (offer.isEnabled() && offer.isActive()) {
                    return price - (price * offer.getDiscountPercentage() / 100);
                }
            }
        }
        return price;






    }



}

package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;
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

    //@Column(name = "status")
    //private String status;

    @Column(name = "stock")
    private String stock;

    private String imageName;

    private String description;

    private String color;

    private String size;

    @ManyToMany(mappedBy = "products")
    private List<Wishlist> wishlists = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "productOfferId" )
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private ProductOffer productOffer;

    private double DiscountedPrice;

    private boolean isDeleted=false;
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



}

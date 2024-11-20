package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "variant")
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "variant_id")
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "product_id", referencedColumnName = "product_id", nullable = false)
//    private Product product;

    @Column(name = "variant_name", nullable = false)
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // Additional fields and methods as needed


//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}

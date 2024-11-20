package com.witcher.e_commerce.application.witcher.dto;

import com.witcher.e_commerce.application.witcher.entity.Variant;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
public class ProductDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long id;

    private String name;

    private Long category;

    private double price;

    private String status;

    private String stock;

    private String color;

    private String size;

    private String imageName;

    private String description;

    private boolean isDeleted=false;
}

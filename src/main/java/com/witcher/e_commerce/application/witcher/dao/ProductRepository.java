package com.witcher.e_commerce.application.witcher.dao;

import com.witcher.e_commerce.application.witcher.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

List<Product> findAllByCategory_Id(Long id);

Page<Product> findAll(Pageable pageable);

Page<Product> findAllByCategoryId(Long category, Pageable pageable);

@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
List<Product> searchProducts(@Param("keyword") String keyword);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);


    // Custom query to find all distinct sizes
    @Query("SELECT DISTINCT p.size FROM Product p WHERE p.size IS NOT NULL")
    List<String> findAllAvailableSizes();

    // Custom query to find all distinct colors
    @Query("SELECT DISTINCT p.color FROM Product p WHERE p.color IS NOT NULL")
    List<String> findAllAvailableColors();


    @Query("SELECT p FROM Product p WHERE " +
            "(:size IS NULL OR p.size = :size) " +
            "AND (:color IS NULL OR p.color = :color)")
    Page<Product> findBySizeAndColor(@Param("size") String size,
                                     @Param("color") String color,
                                     Pageable pageable);


    Page<Product> findBySizeEquals(String size, Pageable pageable);

    Page<Product> findByColorEquals(String color, Pageable pageable);

    Optional<Product> getProductById(Long id);

}

package com.witcher.e_commerce.application.witcher.dao;

import com.witcher.e_commerce.application.witcher.entity.Category;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

    @Query("SELECT o.product.category.name, SUM(o.ItemCount) " +
            "FROM Orders o " +
            "GROUP BY o.product.category.name " +
            "ORDER BY SUM(o.ItemCount) DESC")
    List<Object[]> findTopSellingCategories(Pageable pageable);





}

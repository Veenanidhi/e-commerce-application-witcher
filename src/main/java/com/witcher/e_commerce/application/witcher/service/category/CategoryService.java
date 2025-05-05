package com.witcher.e_commerce.application.witcher.service.category;

import com.witcher.e_commerce.application.witcher.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> findAll();

    public void addCategory(Category category);

    public void deleteCategoryById(Long id);

    public Optional<Category> getCategoryById(Long id);

    Category findByName(String name);

    Page<Category> findAllPaginated(Pageable pageable);

    List<Category> getAllCategories();

}

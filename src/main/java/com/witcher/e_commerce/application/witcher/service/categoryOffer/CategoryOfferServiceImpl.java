package com.witcher.e_commerce.application.witcher.service.categoryOffer;

import com.witcher.e_commerce.application.witcher.dao.CategoryOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.CategoryRepository;
import com.witcher.e_commerce.application.witcher.dto.CategoryOfferDTO;
import com.witcher.e_commerce.application.witcher.entity.Category;
import com.witcher.e_commerce.application.witcher.entity.CategoryOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CategoryOfferServiceImpl implements CategoryOfferService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryOfferRepository categoryOfferRepository;

    @Override
    public CategoryOffer saveCategoryOffer(CategoryOfferDTO categoryOfferDTO) {
        try {
            // Convert DTO to Entity
            CategoryOffer categoryOffer = new CategoryOffer();
            categoryOffer.setCategoryOfferName(categoryOfferDTO.getCategoryOfferName());
            categoryOffer.setDescription(categoryOfferDTO.getDescription());
            categoryOffer.setDiscountPercentage(categoryOfferDTO.getDiscountPercentage());
            categoryOffer.setStartDate(categoryOfferDTO.getStartDate());
            categoryOffer.setExpiryDate(categoryOfferDTO.getExpiryDate());


            Optional<Category> category = categoryRepository.findById(categoryOfferDTO.getCategoryId());
            if (category.isEmpty()) {
                throw new IllegalArgumentException("🚫 Category not found for ID: " + categoryOfferDTO.getCategoryId());
            }
            categoryOffer.setCategory(category.get());


            return categoryOfferRepository.save(categoryOffer);

        } catch (Exception e) {
            System.err.println("❌ Error while saving category offer: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save category offer", e);
        }
    }


    @Override
    public CategoryOffer findCategoryOfferById(Long id) {
        return categoryOfferRepository.findById(id).orElse(null);

    }

    @Override
    public CategoryOffer updateCategoryOffer(CategoryOfferDTO categoryOfferDTO) {
        if (categoryOfferDTO.getCategoryOfferId() == null) {
            throw new IllegalArgumentException("Category Offer ID cannot be null");
        }

        // Fetch the existing offer
        Optional<CategoryOffer> existingOfferOpt = categoryOfferRepository.findById(categoryOfferDTO.getCategoryOfferId());
        if (existingOfferOpt.isEmpty()) {
            throw new NoSuchElementException("Category Offer not found for ID: " + categoryOfferDTO.getCategoryOfferId());
        }

        CategoryOffer existingOffer = existingOfferOpt.get();

        // Update fields
        existingOffer.setCategoryOfferName(categoryOfferDTO.getCategoryOfferName());
        existingOffer.setDescription(categoryOfferDTO.getDescription());
        existingOffer.setDiscountPercentage(categoryOfferDTO.getDiscountPercentage());
        existingOffer.setStartDate(categoryOfferDTO.getStartDate());
        existingOffer.setExpiryDate(categoryOfferDTO.getExpiryDate());

        // Update category relationship
        if (categoryOfferDTO.getCategoryId() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryOfferDTO.getCategoryId());
            if (categoryOpt.isEmpty()) {
                throw new NoSuchElementException("Category not found for ID: " + categoryOfferDTO.getCategoryId());
            }
            existingOffer.setCategory(categoryOpt.get());
        } else {
            throw new IllegalArgumentException("Category ID cannot be null");
        }


        return categoryOfferRepository.save(existingOffer);
    }

    @Override
    public boolean deleteCategoryOfferById(Long id) {
        try {
            if (!categoryOfferRepository.existsById(id)) {
                System.out.println("Category offer with ID " + id + " not found");
                return false;
            }
            categoryOfferRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting category offer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



}



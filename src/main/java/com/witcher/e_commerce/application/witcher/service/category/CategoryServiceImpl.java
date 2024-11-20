package com.witcher.e_commerce.application.witcher.service.category;

import com.witcher.e_commerce.application.witcher.dao.CategoryOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.CategoryRepository;
import com.witcher.e_commerce.application.witcher.dto.OfferDTO;
import com.witcher.e_commerce.application.witcher.entity.Category;
import com.witcher.e_commerce.application.witcher.entity.CategoryOffer;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    private CategoryOfferRepository categoryOfferRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void addCategory(Category category) {
        categoryRepository.save(category);

    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Page<Category> findAllPaginated(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public CategoryOffer saveCategoryOffer(OfferDTO offerDTO) {
        CategoryOffer categoryOffer = new CategoryOffer();
        if (offerDTO.getCategoryIds() != null && !offerDTO.getCategoryIds().isEmpty()){
            List <Category> categories = categoryRepository.findAllById(offerDTO.getCategoryIds());
            for (Category category : categories) {
                categoryOffer.addCategory(category);
            }
        } else {
                System.out.println("no catg to add");
            }



        // Set category offer details from offerDTO
        categoryOffer.setCategoryOfferName(offerDTO.getCategoryOfferName());
        categoryOffer.setDiscountPercentage(offerDTO.getDiscountPercentage());
        categoryOffer.setDescription(offerDTO.getDiscription);
        categoryOffer.setCategories(offerDTO.getCategories());

        // Convert String to LocalDate for expiry date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate expiryDate = LocalDate.parse(offerDTO.getExpiryDate(), formatter);
        LocalDate startDate = LocalDate.parse(offerDTO.getStartDate(), formatter);

        categoryOffer.setStartDate(startDate);
        categoryOffer.setExpiryDate(expiryDate);
        categoryOffer.setActive(true);

        // Save the category offer to the repository
        CategoryOffer savedCategoryOffer = categoryOfferRepository.save(categoryOffer);

        return savedCategoryOffer;
    }

    @Override
    public CategoryOffer findCategoryOfferById(Long id) {
        return categoryOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CategoryOffer not found"));
    }

    @Override
    public CategoryOffer updateCategoryOffer(OfferDTO offerDTO) {
       CategoryOffer categoryOffer = categoryOfferRepository.findById(offerDTO.getCategoryOfferId()).orElse(null);

       if (categoryOffer != null){
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
           categoryOffer.setCategoryOfferName(offerDTO.getCategoryOfferName());
           categoryOffer.setDescription(offerDTO.getDescription());
           categoryOffer.setStartDate(offerDTO.getStartDate(), formatter);
           categoryOffer.setExpiryDate(offerDTO.getExpiryDate(), formatter);
           return categoryOfferRepository.save(categoryOffer);
       }
       return null;
    }

    @Override
    @Transactional
    public boolean deleteCategoryOffer(Long id) {
        if (categoryOfferRepository.existsById(id)) {
            categoryOfferRepository.deleteById(id);
            return true;
        }
        return false;
    }




}

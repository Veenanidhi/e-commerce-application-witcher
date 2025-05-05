package com.witcher.e_commerce.application.witcher.service.categoryOffer;

import com.witcher.e_commerce.application.witcher.dto.CategoryOfferDTO;
import com.witcher.e_commerce.application.witcher.entity.CategoryOffer;

import java.util.List;

public interface CategoryOfferService {

    CategoryOffer saveCategoryOffer(CategoryOfferDTO categoryOfferDTO);

    CategoryOffer findCategoryOfferById(Long id);

    CategoryOffer updateCategoryOffer(CategoryOfferDTO categoryOfferDTO);


    boolean deleteCategoryOfferById(Long id);
}

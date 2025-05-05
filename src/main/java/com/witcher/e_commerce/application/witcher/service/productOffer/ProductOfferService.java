package com.witcher.e_commerce.application.witcher.service.productOffer;

import com.witcher.e_commerce.application.witcher.dto.ProductOfferDTO;
import com.witcher.e_commerce.application.witcher.entity.ProductOffer;

public interface ProductOfferService {


    public ProductOffer saveProductOffer(ProductOfferDTO productOfferDTO);

    public ProductOffer findProductOfferById(Long id);

    public ProductOffer updateProductOffer(ProductOfferDTO productOfferDTO);
}

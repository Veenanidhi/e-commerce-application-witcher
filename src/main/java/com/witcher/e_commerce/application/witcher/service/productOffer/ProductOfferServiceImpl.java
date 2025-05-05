package com.witcher.e_commerce.application.witcher.service.productOffer;

import com.witcher.e_commerce.application.witcher.dao.ProductOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductRepository;
import com.witcher.e_commerce.application.witcher.dto.ProductOfferDTO;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.entity.ProductOffer;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductOfferServiceImpl implements ProductOfferService {

    @Autowired
    private ProductRepository productRepository;

    private final ProductOfferRepository productOfferRepository;
    private final ProductService productService;

    public ProductOfferServiceImpl(ProductOfferRepository productOfferRepository, ProductService productService) {
        this.productOfferRepository = productOfferRepository;
        this.productService = productService;
    }


    @Override
    public ProductOffer saveProductOffer(ProductOfferDTO productOfferDTO) {
        Product product = productService.findProductById(productOfferDTO.getProductId());

        if (product == null) {
            return null; // Product not found, handle this case appropriately
        }

        ProductOffer productOffer = new ProductOffer();
        productOffer.setProductOfferName(productOfferDTO.getProductOfferName());
        productOffer.setDescription(productOfferDTO.getDescription());
        productOffer.setDiscountPercentage(productOfferDTO.getDiscountPercentage());
        productOffer.setStartDate(productOfferDTO.getStartDate());
        productOffer.setExpiryDate(productOfferDTO.getExpiryDate());


        productOffer.getProducts().add(product);
        if (productOfferDTO.getProductId() != null) {
            System.out.println("saveeeeeeeeeeeeeeeeeeeee producttttt" + product.getId());
        }else {
            System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
        }

        productOffer = productOfferRepository.save(productOffer);


        product.getProductOffers().add(productOffer);
        productRepository.save(product);

        return productOffer;
    }

    @Override
    public ProductOffer findProductOfferById(Long id) {
        Optional<ProductOffer> productOfferOptional = productOfferRepository.findById(id);

        // Return the ProductOffer if it exists, or null if it doesn't
        return productOfferOptional.orElse(null);
    }

    @Override
    public ProductOffer updateProductOffer(ProductOfferDTO productOfferDTO) {
        ProductOffer existingOffer = productOfferRepository.findById(productOfferDTO.getProductOfferId())
                .orElseThrow(()-> new IllegalArgumentException("Product offer not found with ID: " + productOfferDTO.getProductOfferId()));


        existingOffer.setProductOfferName(productOfferDTO.getProductOfferName());
        existingOffer.setDescription(productOfferDTO.getDescription());
        existingOffer.setDiscountPercentage(productOfferDTO.getDiscountPercentage());
        existingOffer.setStartDate(productOfferDTO.getStartDate());
        existingOffer.setExpiryDate(productOfferDTO.getExpiryDate());

        existingOffer.getProducts().clear();

        if (productOfferDTO.getProducts() != null) {
            for (Long productId : productOfferDTO.getProducts()) {
                Product product = productService.findProductById(productId);  // Fetch the product by ID
                if (product != null) {
                    existingOffer.getProducts().add(product);  // Add the product to the offer's products
                }
            }
        }


        return productOfferRepository.save(existingOffer);
    }






}

package com.witcher.e_commerce.application.witcher.service.product;

import com.witcher.e_commerce.application.witcher.dao.ProductOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductRepository;
import com.witcher.e_commerce.application.witcher.dto.OfferDTO;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.entity.ProductOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final ProductOfferRepository productOfferRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductOfferRepository productOfferRepository) {
        this.productRepository = productRepository;
        this.productOfferRepository = productOfferRepository;
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public void addProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void removeProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAllProductsByCategoryId(Long id) {
        return productRepository.findAllByCategory_Id(id);
    }

    @Override
    public ProductOffer saveProductOffer(OfferDTO offerDTO) {

        ProductOffer productOffer= new ProductOffer();
        productOffer.setProductOfferName(offerDTO.getProductOfferName());
        productOffer.setDiscountPercentage(offerDTO.getDiscountPercentage());
        productOffer.setDescription(offerDTO.getDescription());


        // Convert String to LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Ensure the format matches the date string
        LocalDate expiryDate = LocalDate.parse(offerDTO.getExpiryDate(), formatter);
        LocalDate startDate = LocalDate.parse(offerDTO.getStartDate(), formatter);

        productOffer.setExpiryDate(expiryDate);
        productOffer.setStartDate(startDate);

        productOffer.setActive(true);

        // Save the product offer
        ProductOffer savedOffer = productOfferRepository.save(productOffer);

        return savedOffer;
}

    @Override
    public Page<Product> findProductsByCategory(Long category, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.findAllByCategoryId(category,pageable);
    }

    @Override
    public Page<Product> findAllProducts(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }



    @Override
    public Page<Product> searchProductsByNameOrDescription(String searchTerm, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm, pageable);
    }

    @Override
    public Page<Product> findProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findProductsBySizeAndColor(String size, String color, Pageable pageable) {
        return productRepository.findBySizeAndColor(size, color, pageable);
    }

    @Override
    public List<String> findAllAvailableSizes() {
        return productRepository.findAllAvailableSizes();
    }

    @Override
    public List<String> findAllAvailableColors() {
        return productRepository.findAllAvailableColors();
    }

    @Override
    public Page<Product> findProductsBySizeEquals(String size, Pageable pageable) {
        return productRepository.findBySizeEquals(size, pageable);
    }

    @Override
    public Page<Product> findProductsByColorEquals(String color, Pageable pageable) {
        return productRepository.findByColorEquals(color, pageable);
    }


    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public ProductOffer updateProductOffer(OfferDTO offerDTO) {
        ProductOffer existingOffer = productOfferRepository.findById(offerDTO.getProductOfferId())
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        // Update properties

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(offerDTO.getStartDate(), formatter);
        LocalDate expiryDate = LocalDate.parse(offerDTO.getExpiryDate(), formatter);
        existingOffer.setProductOfferName(offerDTO.getProductOfferName());
        existingOffer.setDiscountPercentage(offerDTO.getDiscountPercentage());
        existingOffer.setDescription(offerDTO.getDescription());
        existingOffer.setProductList(offerDTO.getProductList());
        existingOffer.getProductList();

        return productOfferRepository.save(existingOffer);
    }

    @Override
    public boolean deleteProductOffer(Long id) {
        if (productOfferRepository.existsById(id)) {
            productOfferRepository.deleteById(id);
            return true;
        }
        return false;
    }


}
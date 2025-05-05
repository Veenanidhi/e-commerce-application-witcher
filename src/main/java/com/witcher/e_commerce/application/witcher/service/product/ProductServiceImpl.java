package com.witcher.e_commerce.application.witcher.service.product;

import com.witcher.e_commerce.application.witcher.controller.exception.ProductNotFoundException;
import com.witcher.e_commerce.application.witcher.dao.ProductOfferRepository;
import com.witcher.e_commerce.application.witcher.dao.ProductRepository;
import com.witcher.e_commerce.application.witcher.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    public boolean deleteProductOffer(Long id) {
        if (productOfferRepository.existsById(id)) {
            productOfferRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Product findProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isPresent()){
            System.out.println("productttttttttttttttttttttttt" +productOptional.get().getName());
        }

        // Return the Product if it exists, or throw an exception if not found
        return productOptional.orElseThrow(() -> new ProductNotFoundException("Product not found for ID: " + productId));
    }

    @Override
    public List<Product> getProductsByCategoryKeyword(String keyword, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return productRepository.findByCategory_NameContainingIgnoreCase(keyword, sort);
    }

    public Page<Product> filterProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    public Page<Product> filterProductsByPriceContaining(String priceSearchTerm, Pageable pageable) {
        return productRepository.findByPriceContaining(priceSearchTerm, pageable);
    }







}
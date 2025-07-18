package com.witcher.e_commerce.application.witcher.service.product;

import com.witcher.e_commerce.application.witcher.dto.OfferDTO;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.entity.ProductOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

public List<Product> getAllProduct();

public Page<Product> getProducts(Pageable pageable);

public void addProduct(Product product);

public void removeProductById(Long id);

public Optional <Product> getProductById(Long id);

public List<Product> getAllProductsByCategoryId(Long id);

    ProductOffer saveProductOffer(OfferDTO offerDTO);

    Page<Product> findProductsByCategory(Long category, int page, int pageSize);

    Page<Product> findAllProducts(int page, int pageSize);

    Page<Product> getAllProducts(Pageable pageable);

    List<Product> searchProducts(String keyword);

    Page<Product> searchProductsByNameOrDescription(String searchTerm, Pageable pageable);

    Page<Product> findProductsByCategory(Long categoryId, Pageable pageable);

    Page<Product> findAllProducts(Pageable pageable);

    Page<Product> findProductsBySizeAndColor(String size, String color, Pageable pageable);

    List<String> findAllAvailableSizes();

    List<String> findAllAvailableColors();

    Page<Product> findProductsBySizeEquals(String size, Pageable pageable);

    Page<Product> findProductsByColorEquals(String color, Pageable pageable);


    void saveProduct(Product product);

    ProductOffer updateProductOffer(OfferDTO offerDTO);

    boolean deleteProductOffer(Long id);
}

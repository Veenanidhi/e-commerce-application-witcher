package com.witcher.e_commerce.application.witcher.service.wishlist;

import com.witcher.e_commerce.application.witcher.dao.WishlistRepository;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.Wishlist;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import com.witcher.e_commerce.application.witcher.service.wishlist.WishlistService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    private final ProductService productService;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, ProductService productService) {
        this.wishlistRepository = wishlistRepository;
        this.productService = productService;
    }


    @Override
    public Wishlist getUserWishlist(User user) {

        return wishlistRepository.findByUser(user).orElseGet(() -> {
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            return wishlistRepository.save(wishlist);
        });
    }

    @Override
    public void addProductToWishlist(User user, Long productId) {

        Wishlist wishlist = getUserWishlist(user);
        Optional<Product> optionalProduct = productService.getProductById(productId);

        optionalProduct.ifPresent(wishlist::addProduct);
        wishlistRepository.save(wishlist);

    }

    @Override
    public void removeProductFromWishlist(User user, Long productId) {
        Wishlist wishlist = getUserWishlist(user);
        Optional<Product> optionalProduct = productService.getProductById(productId);

        optionalProduct.ifPresent(wishlist::removeProduct);
        wishlistRepository.save(wishlist);

    }

    @Override
    public boolean isProductInWishlist(User user, Long productId) {
        Wishlist wishlist = getUserWishlist(user);
        return wishlist.getProducts().stream()
                .anyMatch(product -> product.getId().equals(productId));
    }
}

package com.witcher.e_commerce.application.witcher.service.wishlist;

import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.Wishlist;

public interface WishlistService {


    Wishlist getUserWishlist(User user);

    void addProductToWishlist(User user, Long productId);

    void removeProductFromWishlist(User user, Long productId);

    boolean isProductInWishlist(User user, Long productId);
}

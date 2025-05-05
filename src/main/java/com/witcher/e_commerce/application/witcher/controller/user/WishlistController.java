package com.witcher.e_commerce.application.witcher.controller.user;

import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.Wishlist;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.wishlist.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/wishlist")
public class WishlistController {

    private final UserService userService;

    private final WishlistService wishlistService;


    public WishlistController(UserService userService, WishlistService wishlistService) {
        this.userService = userService;
        this.wishlistService = wishlistService;
    }

    @GetMapping("/")
    public String getWishlist(Model model) {
        User user = userService.getCurrentUser();
        Wishlist wishlistItems = wishlistService.getUserWishlist(user);

        model.addAttribute("wishlistItems", wishlistItems.getProducts());
        return "wishlist";
    }

    @PostMapping("/add/{productId}")
    public String addProductToWishlist(@PathVariable Long productId, Model model) {
        User user = userService.getCurrentUser();

        // Check if the product is already in the wishlist
        if (wishlistService.isProductInWishlist(user, productId)) {
            model.addAttribute("message", "Product is already in your wishlist.");
        } else {
            wishlistService.addProductToWishlist(user, productId);
            model.addAttribute("message", "Product added to your wishlist.");
        }

        return "redirect:/user/wishlist/";
    }

    @PostMapping("/remove/{productId}")
    public String removeProductFromWishlist(@PathVariable Long productId) {
        User user = userService.getCurrentUser();
        wishlistService.removeProductFromWishlist(user, productId);

        return "redirect:/user/wishlist/";
    }




}

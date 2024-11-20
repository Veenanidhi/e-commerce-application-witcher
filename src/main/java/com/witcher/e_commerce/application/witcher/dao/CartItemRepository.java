package com.witcher.e_commerce.application.witcher.dao;

import com.witcher.e_commerce.application.witcher.entity.Cart;
import com.witcher.e_commerce.application.witcher.entity.CartItem;
import com.witcher.e_commerce.application.witcher.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find a cart item by cart and product
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Optional<CartItem> findById(Long id);


}

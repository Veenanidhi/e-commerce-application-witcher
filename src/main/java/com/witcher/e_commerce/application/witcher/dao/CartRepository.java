package com.witcher.e_commerce.application.witcher.dao;

import com.witcher.e_commerce.application.witcher.entity.Cart;
import com.witcher.e_commerce.application.witcher.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Find a cart by the user
    Optional<Cart> findByUser(User user);

    Cart findByUserId(Long userId);
}

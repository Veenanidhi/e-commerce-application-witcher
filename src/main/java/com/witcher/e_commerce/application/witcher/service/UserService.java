package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{

    User findById(Long id);

    void deleteById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    User registerUser(User user);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    User save(User user);

    void enableUser(String email);

    User getCurrentUser();

    void updateUser(User user);

//reset pass old
    void updatePassword(String token, String newPassword);

    boolean validateResetToken(String token);

    String generateResetToken(String email);

}

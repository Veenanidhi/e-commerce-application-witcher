package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import com.witcher.e_commerce.application.witcher.dao.VerificationTokenRepository;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.VerificationToken;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenService verificationTokenService;

    private final VerificationTokenRepository tokenRepository;

    private final EmailService emailService;



    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder, VerificationTokenService tokenService, VerificationTokenRepository tokenRepository, EmailService emailService){
        this.userRepository=userRepository;
        this.passwordEncoder=bCryptPasswordEncoder;

        this.verificationTokenService = tokenService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Override
    public User findById(Long id) {
        Optional<User>user1=userRepository.findById(id);
        if (user1.isPresent()){
            return user1.get();
        }

        return null;
    }

    @Override
    @Transactional
    public User registerUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.count() >1){
            user.setRole(Role.ROLE_USER);
        }
        else{
            user.setRole(Role.ROLE_ADMIN);
        }

        //to disable new user before activation
        user.setEnabled(false);
        log.info("USER BEFORE SAVING :{}",user);
        Optional<User> saved = Optional.of( save(user));
/*
        // Create and save verification token if the user is saved
        saved.ifPresent(u -> {
            try {
                System.out.println("dne");
                String token = UUID.randomUUID().toString();
                VerificationToken verificationToken = new VerificationToken(token,u);
                tokenRepository.save(verificationToken);

                // Send verification email
                emailService.sendHtmlMail(u);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

 */
        return saved.get();

    }


    @Override
    public void deleteById(Long id) {
      userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();

    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }



    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
       return userRepository.save(user);


    }

    @Override
    public void enableUser(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }

    }

    @Override
    public User getCurrentUser() {
        // Get the authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        String username = authentication.getName(); // Assuming the principal is the username

        // Fetch user from database by username
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public void updateUser(User user) {
        // Get the currently logged-in user
        User currentUser = getCurrentUser();

        // Update fields with new values
        currentUser.setEmail(user.getEmail());
        currentUser.setUsername(user.getUsername());

        // Save updated user to the database
        userRepository.save(currentUser);
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional= userRepository.findByEmail(email);
        if (userOptional.isEmpty()){
            throw new UsernameNotFoundException("User not found with this email" +email);
        }
        User user= userOptional.get();

        return new CustomUserDetails(user);
    }


}

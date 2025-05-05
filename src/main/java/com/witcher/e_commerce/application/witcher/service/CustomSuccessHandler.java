package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import com.witcher.e_commerce.application.witcher.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Authentication status: " + authentication.getAuthorities());

        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            handleOAuth2User(authentication, response);
        } else if (authentication.getPrincipal() instanceof CustomUserDetails) {
            handleCustomUser(authentication, response);
        } else {
            log.warn("Unexpected principal type: " + authentication.getPrincipal().getClass());
            response.sendRedirect("/access-denied");
        }
    }

    private void handleOAuth2User(Authentication authentication, HttpServletResponse response) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        // Print all attributes for debugging
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("User attributes: " + attributes);

        // Extract email and name using correct keys
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");
        String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "").trim();

//        String name = (String) attributes.get("given_name");
//        String fullName= (String) attributes.get("name");




        User user = userRepository.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setUsername(fullName.isEmpty() ? email : fullName); // Fallback to email if full name is empty
        user.setEnabled(true);

        if (user.getId() == null) {
            user.setPassword("OAUTH2_USER");
            userRepository.save(user);
            log.info("New OAuth2 user saved: " + email);
        } else {
            userRepository.save(user);
            log.info("Existing OAuth2 user updated: " + email);
        }

//        // Update the name attribute
//        attributes.put("name", user.getUsername());
//
//        // Create a new OAuth2User with updated attributes
//        OAuth2User newOAuth2User = new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "name");
//
//        // Update the Authentication object
//        OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(newOAuth2User,
//                authentication.getAuthorities(),
//                ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());
//
//        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectBasedOnRoles(authentication, response);

    }

    public String getLoggedInUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private void handleCustomUser(Authentication authentication, HttpServletResponse response) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("Custom user authenticated: " + userDetails.getUsername());

        redirectBasedOnRoles(authentication, response);
    }


    private void redirectBasedOnRoles(Authentication authentication, HttpServletResponse response) throws IOException {
        var authorities = authentication.getAuthorities();
        var roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_USER") || roles.contains("OIDC_USER")) {
            response.sendRedirect("/landingPage");
        } else {
            response.sendRedirect("/access-denied");
        }
    }
}















//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
//    }

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        log.info("authentication status?" +authentication.getAuthorities());
//
//        var authourities= authentication.getAuthorities();
//        var roles= authourities.stream().map(r -> r.getAuthority()).findFirst();
//
//        if (roles.orElse(" ").equals("ROLE_ADMIN")){
//            response.sendRedirect("/admin/dashboard");
//        } else if (roles.orElse(" ").equals("ROLE_USER, OIDC_USER")) {
//            response.sendRedirect("/productPage");
//        }else{
//            response.sendRedirect("/access-denied");
//        }
//
//    }

package com.witcher.e_commerce.application.witcher.controller;

import com.witcher.e_commerce.application.witcher.cartlogic.CartData;
import com.witcher.e_commerce.application.witcher.entity.Category;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.service.EmailService;
import com.witcher.e_commerce.application.witcher.service.OTPService;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.category.CategoryService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
public class AuthController {


    private final UserService userService;

    private final EmailService emailService;

    private final OTPService otpService;

    private final CategoryService categoryService;

    private final ProductService productService;


    @Autowired
    public AuthController(UserService userService, EmailService emailService, OTPService otpService, CategoryService categoryService, ProductService productService){
        this.userService=userService;
        this.emailService = emailService;
        this.otpService = otpService;
        this.categoryService = categoryService;
        this.productService = productService;
    }


    @GetMapping("/login")
    public String showLogin(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CartData.cart.clear();
        System.out.println( authentication.getPrincipal());

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object authenticatedAttribute = session.getAttribute("authenticated");
                if (authenticatedAttribute instanceof Boolean && (Boolean) authenticatedAttribute) {
                    return "redirect:/productPage";
                }
                session.removeAttribute("registration");
            }
            return "login";
        }

        return "redirect:/productPage";
    }



    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }



    @GetMapping("/signup")
    public String signUpController(Model model, HttpSession session){
        // Initialize a session attribute to track the signup process
        session.setAttribute("signupInProgress", true);
        model.addAttribute("webUser", new User());
        return "signup";

    }

    @PostMapping("/processSignUpPage")
    public String processSignUpPage(
            @ModelAttribute("webUser") User theWebUser,
            @RequestParam(value = "referralCode", required = false) String referralCode,
            BindingResult theBindingResult,
            RedirectAttributes ra,
            Model theModel,
            HttpSession session
    ){



        Boolean signupInProgress = (Boolean) session.getAttribute("signupInProgress");
        if (signupInProgress == null || !signupInProgress) {
            return "redirect:/signup";
        }


        if (theBindingResult.hasErrors()) {
            return "signup";
        }

        //to check the username is already taken
//        boolean existingUser=userService.existsByUsername(theWebUser.getUsername());
//        if (existingUser){
//            theModel.addAttribute("webUser",new User());
//            theModel.addAttribute("signupError","Username already exists!!!");
//            return "redirect:/signup";
//        }
//
//        boolean existingEmail= userService.existsByEmail(theWebUser.getEmail());
//        if (existingEmail){
//            theModel.addAttribute("message","Email already exists");
//            return "signup";
//        }

        boolean existingUser = userService.existsByUsername(theWebUser.getUsername());
        if (existingUser) {

            theBindingResult.rejectValue("username", "error.username", "Username already exists!");
            return "signup";
        }

        // Email validation - consistent approach
        boolean existingEmail = userService.existsByEmail(theWebUser.getEmail());
        if (existingEmail) {
            theBindingResult.rejectValue("email", "error.email", "Email already exists!");
            return "signup"; // Return to form with error
        }


        userService.registerUser(theWebUser);


        String otp= otpService.generateOTP(theWebUser.getEmail());
        emailService.sendOTPEmail(theWebUser.getEmail(),otp);


        ra.addFlashAttribute("message", "An activation mail is sent to your email. Please enter the OTP to verify your account");
        return "redirect:/verify-otp?email=" +theWebUser.getEmail();
    }

    @GetMapping("/landingPage")
    public String landingPage(Model model){

        List<Product> allProducts = productService.getAllProduct();
        List<Product> featuredProducts = allProducts.stream()
                .sorted(Comparator.comparing(Product::getId).reversed()) // Sort by ID desc
                .limit(4)
                .toList();

        List<Category> categories = categoryService.findAll();

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("categories", categories);

        return "landingPage";
    }


    @GetMapping("/access-denied")
    public String showAccessDenied() {

        return "access-denied";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "product-list";
    }

    @GetMapping("/productPage")
    public  String showShop(Model model, HttpSession session,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int pageSize,
                            @RequestParam(name ="category",required=false) Long category,
                            @RequestParam(name = "searchTerm", required = false) String searchTerm,
                            @RequestParam(name = "size", required = false) String size,
                            @RequestParam(name = "color", required = false) String color,
                            @RequestParam(name = "minPrice", required = false) Double minPrice,
                            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
                            @RequestParam(name = "price", required = false) String priceSearchTerm,
                            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir ) {



        model.addAttribute("cartCount", CartData.cart.size());


        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);



        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Product> productsPage;


        if (searchTerm != null && !searchTerm.isEmpty()) {

//            productsPage = productService.searchProductsByNameOrDescription(searchTerm, pageable);
            try {
                // Check if the search term is a number → treat as price
                Double.parseDouble(searchTerm);
                productsPage = productService.filterProductsByPriceContaining(searchTerm, pageable);
            } catch (NumberFormatException e) {
                // Not a number → treat as name/description
                productsPage = productService.searchProductsByNameOrDescription(searchTerm, pageable);
            }
            model.addAttribute("searchTerm", searchTerm);
        } else if (category != null) {
            productsPage = productService.findProductsByCategory(category, pageable);
            model.addAttribute("category", category);
        }
        else if (size != null && !size.isEmpty() && color != null && !color.isEmpty()) {
            productsPage = productService.findProductsBySizeAndColor(size, color, pageable);
            model.addAttribute("selectedSize", size);
            model.addAttribute("selectedColor", color);
        }
        else if (size != null && !size.isEmpty()) {
            productsPage = productService.findProductsBySizeEquals(size, pageable);
            model.addAttribute("selectedSize", size);
        } else if (color != null && !color.isEmpty()) {
            productsPage = productService.findProductsByColorEquals(color, pageable);
            model.addAttribute("selectedColor", color);
        }
        else if (minPrice != null && maxPrice != null) {
            productsPage = productService.filterProductsByPriceRange(minPrice, maxPrice, pageable);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        }
        else {
            productsPage = productService.findAllProducts(pageable);
        }


        List<String> availableSizes = productService.findAllAvailableSizes();
        List<String> availableColors = productService.findAllAvailableColors();





        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentDate", LocalDate.now());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("sizes", availableSizes);
        model.addAttribute("colors", availableColors);


        return "product-listing";
    }

    @GetMapping("/products/forHer")
    public String getForHerProducts(Model model,
                                    @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                    @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        List<Product> products = productService.getProductsByCategoryKeyword("women", sortBy, sortDir);
        model.addAttribute("products", products);
        return "product-listing";
    }

    @GetMapping("/products/forHim")
    public String getForHimProducts(Model model,
                                    @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
                                    @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        List<Product> products = productService.getProductsByCategoryKeyword("men", sortBy, sortDir);
        model.addAttribute("products", products);
        return "product-listing";
    }





    @GetMapping("/productPage/category/{id}")
    public String productByCategory(Model model, @PathVariable Long id){
        model.addAttribute("cartCount", CartData.cart.size());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("products", productService.getAllProductsByCategoryId(id));

        return "product-listing";
    }

    @GetMapping("/productList/viewProduct/{id}")
    public String viewProduct(Model model, @PathVariable Long id){

        model.addAttribute("cartCount", CartData.cart.size());
        model.addAttribute("product", productService.getProductById(id).get());
        return "view-product";
    }



    @GetMapping("/aboutPage")
    public String aboutPage(){
        return "about-page";
    }

    @GetMapping("/contactPage")
    public String contactPage(){
        return "contact-page";
    }
















//    @GetMapping("/forgot-password")
//    public String showForgotPasswordPage() {
//        return "forgot-password";
//    }
//
//    @PostMapping("/forgot-password")
//    public String processForgotPassword(@RequestParam("email") String email, Model model) {
//        try {
//            String token = userService.generateResetToken(email);
//            String resetLink = "http://localhost:8080/reset-password?token=" + token;
//            emailService.sendEmail(email, "Password Reset", "Click here to reset your password: " + resetLink);
//            model.addAttribute("message", "A password reset link has been sent to your email.");
//        } catch (Exception e) {
//            model.addAttribute("error", "Email address not found.");
//        }
//        return "forgot-password";
//    }
//
//    @GetMapping("/reset-password")
//    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
//        System.out.println("Received Token: " + token);
//        boolean isValidToken = userService.validateResetToken(token);
//        if (!isValidToken) {
//            model.addAttribute("error", "Invalid or expired token.");
//            return "error-page";
//        }
//        model.addAttribute("token", token);
//        return "reset-password";
//    }
//
//    @PostMapping("/reset-password")
//    public String processResetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword, Model model) {
//        try {
//            userService.updatePassword(token, newPassword);
//            model.addAttribute("message", "Password successfully reset.");
//            return "login";
//        } catch (Exception e) {
//            model.addAttribute("error", "Error resetting password.");
//            return "reset-password";
//        }
//    }
//


}
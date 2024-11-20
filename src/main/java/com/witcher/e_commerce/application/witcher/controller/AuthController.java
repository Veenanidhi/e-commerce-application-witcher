package com.witcher.e_commerce.application.witcher.controller;

import com.witcher.e_commerce.application.witcher.cartlogic.CartData;
import com.witcher.e_commerce.application.witcher.entity.Product;
import com.witcher.e_commerce.application.witcher.entity.ProductOffer;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.service.EmailService;
import com.witcher.e_commerce.application.witcher.service.OTPService;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.category.CategoryService;
import com.witcher.e_commerce.application.witcher.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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




//    @GetMapping("/login")
//    public String showLogin(){
//
//        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
//
//        if(authentication==null || authentication instanceof AnonymousAuthenticationToken){
//            return "login";
//        }
//
//        return "redirect:/product-listing";
//
//    }

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
            BindingResult theBindingResult,
            RedirectAttributes ra,
            Model theModel,
            HttpSession session
    ){


        // Check if signup is in progress
        Boolean signupInProgress = (Boolean) session.getAttribute("signupInProgress");
        if (signupInProgress == null || !signupInProgress) {
            return "redirect:/signup"; // Redirect if session is not initialized properly
        }


        if (theBindingResult.hasErrors()) {
            return "signup"; // Return to the signup page if there are validation errors
        }

        //to check the username is already taken
        boolean existingUser=userService.existsByUsername(theWebUser.getUsername());
        if (existingUser){
            theModel.addAttribute("webUser",new User());
            theModel.addAttribute("signupError","Username already exists!!!");
            return "redirect:/signup";
        }

        boolean existingEmail= userService.existsByEmail(theWebUser.getEmail());
        if (existingEmail){
            theModel.addAttribute("message","Email already exists");
            return "signup";
        }

        //save the new user
        userService.registerUser(theWebUser);

        //generate and snd otp
        String otp= otpService.generateOTP(theWebUser.getEmail());
        emailService.sendOTPEmail(theWebUser.getEmail(),otp);

        //redirect to otp verification pge with a success msg
        ra.addFlashAttribute("message", "An activation mail is sent to your email. Please enter the OTP to verify your account");
        return "redirect:/verify-otp?email=" +theWebUser.getEmail();
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
                            @RequestParam(defaultValue = "28") int pageSize,
                            @RequestParam(name ="category",required=false) Long category,
                            @RequestParam(name = "searchTerm", required = false) String searchTerm,
                            @RequestParam(name = "size", required = false) String size,
                            @RequestParam(name = "color", required = false) String color,
                            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy, //Default sort by id
                            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir ) {  //default sort direction



        model.addAttribute("cartCount", CartData.cart.size());

        // Create a Sort object using sortBy and sortDir
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);


        // Create a Pageable object using page and pageSize
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Product> productsPage;

        if (searchTerm != null && !searchTerm.isEmpty()) {
            productsPage = productService.searchProductsByNameOrDescription(searchTerm, pageable);
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
        else {
            productsPage = productService.findAllProducts(pageable);
        }




        // Fetch available sizes and colors for filters
        List<String> availableSizes = productService.findAllAvailableSizes();
        List<String> availableColors = productService.findAllAvailableColors();


        Map<Long, Double> discountedPrices = new HashMap<>();

        for (Product product : productsPage.getContent()) {
            ProductOffer offer = product.getProductOffer();
            if (offer != null && offer.isEnabled() && offer.isActive() &&
                    LocalDate.now().isAfter(offer.getStartDate()) &&
                    LocalDate.now().isBefore(offer.getExpiryDate())) {

                double discount = offer.getDiscountPercentage();
                double discountedPrice = product.getPrice() * (1 - discount / 100);
                discountedPrices.put(product.getId(), discountedPrice);
            }
        }

        for (Product product : productsPage.getContent()) {
            String categoryOfferName;
            if (product.getCategory().getCategoryOffer() != null && product.getCategory().getCategoryOffer().getDiscountPercentage() != null) {
                categoryOfferName = String.valueOf(product.getCategory().getCategoryOffer().getDiscountPercentage());
            } else {
                categoryOfferName = "no offer";
            }

//            System.out.println("product name = " + product.getName()
//                    + " category name = " + product.getCategory().getName()
//                    + " category offer = " + categoryOfferName);
        }



        //  model.addAttribute("categoryList",categoryList);
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("sizes", availableSizes); // Add sizes to model
        model.addAttribute("colors", availableColors); // Add colors to model

//        Integer cartCount = (Integer) session.getAttribute("cartCount");
//        int cartCountValue = (cartCount != null) ? cartCount.intValue() : 0;
//        model.addAttribute("cartCount", cartCountValue);

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












}




























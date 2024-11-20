package com.witcher.e_commerce.application.witcher.controller.user;

import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.WalletService;
import com.witcher.e_commerce.application.witcher.service.address.AddressService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private WalletService walletService;

    private final UserService userService;

    private final OrderService orderService;

    public UserProfileController(AddressService addressService, UserService userService, OrderService orderService) {
        this.addressService = addressService;
        this.userService = userService;
        this.orderService = orderService;
    }


    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        User user = null;

        // Check if the principal is an OAuth2 user
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
            String email = oauthToken.getPrincipal().getAttribute("email"); // Or the relevant attribute key for email
            user = userService.findByEmail(email); // Adjust this method to fetch the user using their email address
        } else {
            // Handle regular users
            user = getCurrentUser(principal);
        }

        // Fetch all addresses for the user
        List<Address> addresses = addressService.getAllAddressesByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("addresses", addresses);
        System.out.println("Fetched addresses: " + addresses); // Debugging
        return "user-profile";
    }




    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user, Model model){

       userService.updateUser(user);
       model.addAttribute("user", user);

       return "redirect:/user/profile";
    }




    @GetMapping("/address/add")
    public String addAddressForm(Model model) {
        model.addAttribute("address", new Address());

        return "add-address";
    }


    @PostMapping("/address")
    public String saveAddress(@ModelAttribute("address") Address address, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "add-address"; // Show form with errors
        }

        User user = getCurrentUser(principal);
        address.setUser(user);
        addressService.saveOrUpdateAddress(address);
        return "redirect:/user/profile";
    }




    @GetMapping("/address/edit/{id}")
    public String editAddressForm(@PathVariable Long id, Model model, Principal principal) {
        User user = getCurrentUser(principal);
        Address address = addressService.getAddressById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid address ID: " + id));

        // Ensure the address belongs to the user
        if (!address.getUser().getId().equals(user.getId())) {
            return "redirect:/user/profile?error=notAuthorized"; // Redirect if not authorized
        }

        model.addAttribute("address", address);
        return "edit-address"; // Your edit address template
    }



    @PostMapping("/address/update")
    public String updateAddress(@ModelAttribute("address") Address address, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "edit-address"; // Show form with errors if validation fails
        }
        User user = getCurrentUser(principal);
        address.setUser(user); // Ensure the address is associated with the correct user
        addressService.saveOrUpdateAddress(address); // Save or update the address
        return "redirect:/user/profile"; // Redirect to profile after update
    }

    @PostMapping("/address/delete/{id}")
    public String deleteAddress(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);

        // Fetch the address by ID and ensure the address belongs to the current user
        Address address = addressService.getAddressById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid address ID: " + id));

        // Ensure the address belongs to the user
        if (!address.getUser().getId().equals(user.getId())) {
            return "redirect:/user/profile?error=notAuthorized"; // If not authorized
        }

        // Delete the address
        addressService.deleteAddress(id);

        return "redirect:/user/profile"; // Redirect back to the profile page
    }




    // Helper method to get the current authenticated user
    private User getCurrentUser(Principal principal) {
        // Implement logic to retrieve the authenticated user from the database
        return userService.findByUsername(principal.getName()); // Replace with actual user fetching logic

    }

    // Fetch order history for the currently logged-in user
    @GetMapping("/order-history")
    public String getUserOrderHistory(Model model, Authentication authentication,
                                      @RequestParam(defaultValue = "1") int page) {
        String username = authentication.getName(); // Get the username of the logged-in user
        List<Orders> orders = orderService.getOrdersByUser(username, page);
        int totalPages = orderService.getTotalPagesByUser(username); // Pagination logic

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "order-history"; // This corresponds to Thymeleaf template (order-history.html)
    }

    @PostMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);

        // Fetch the order and ensure it belongs to the user
        Orders order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/user/profile/order-history?error=notAuthorized";
        }

        // Check if the order is not delivered and cancel it
        if (!order.getOrderStatus().equals("Delivered")) {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order cancelled successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot cancel delivered order.");
        }

        return "redirect:/user/profile/order-history";
    }

    @PostMapping("/order/return/{id}")
    public String returnOrder(@PathVariable Long id, Principal principal,
                              RedirectAttributes redirectAttributes){

        User user= getCurrentUser(principal);

        //fetching the order to ensure it belongs to the user
        Orders order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/user/profile/order-history?error=notAuthorized";
        }

        // allow return if the order has been delivered
        if (order.getOrderStatus().equals("Delivered")){
            orderService.returnOrder(id); // Logic to handle returning the order
            redirectAttributes.addFlashAttribute("successMessage", "Order returned successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot return an undelivered order.");
        }

            return "redirect:/user/profile/order-history";
        }

//    @GetMapping("/wallet")
//    public String showWalletPage(Model model, @RequestParam Long userId) {
//        Wallet wallet = walletService.getWalletByUserId(userId);
//        model.addAttribute("wallet", wallet);
//        return "wallet"; // Make sure this corresponds to the wallet HTML view file (walletPage.html).
//    }

    @GetMapping("/wallet")
    public String showWalletPage(Model model, @RequestParam Long userId) {
        try {
            Wallet wallet = walletService.getWalletByUserId(userId);
            model.addAttribute("wallet", wallet);
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "wallet";
        }
        return "wallet";
    }



    @PostMapping("/wallet/create/{userId}")
    public ResponseEntity<Wallet> createWallet(@PathVariable Long userId) {
        Wallet wallet = walletService.createWallet(userId);
        return ResponseEntity.ok(wallet);
    }

    // Get wallet by user ID
    @GetMapping("/wallet/user/{userId}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable Long userId) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(wallet);
    }

    // Update wallet balance
    @PutMapping("/wallet/updateBalance/{walletId}")
    public ResponseEntity<Wallet> updateBalance(@PathVariable Long walletId, @RequestParam Double amount) {
        Wallet wallet = walletService.updateBalance(walletId, amount);
        return ResponseEntity.ok(wallet);
    }

    // Set referral used
    @PutMapping("/wallet/setReferralUsed/{walletId}")
    public ResponseEntity<Wallet> setReferralUsed(@PathVariable Long walletId) {
        Wallet wallet = walletService.setReferralUsed(walletId);
        return ResponseEntity.ok(wallet);
    }

    // Add a transaction to the wallet
    @PostMapping("/wallet/addTransaction/{walletId}")
    public ResponseEntity<Wallet> addTransaction(@PathVariable Long walletId, @RequestBody Transaction transaction) {
        Wallet wallet = walletService.addTransaction(walletId, transaction);
        return ResponseEntity.ok(wallet);
    }



}












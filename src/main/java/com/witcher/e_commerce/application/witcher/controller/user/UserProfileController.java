package com.witcher.e_commerce.application.witcher.controller.user;

import com.witcher.e_commerce.application.witcher.dao.OrderRepository;
import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import com.witcher.e_commerce.application.witcher.dao.WalletRepository;
import com.witcher.e_commerce.application.witcher.entity.*;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.WalletTransactionService;
import com.witcher.e_commerce.application.witcher.service.address.AddressService;
import com.witcher.e_commerce.application.witcher.service.order.OrderService;
import com.witcher.e_commerce.application.witcher.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/user/profile")

public class UserProfileController {


    @Autowired
    private AddressService addressService;

    private final UserService userService;

    private final WalletService walletService;

    private final OrderService orderService;

    private final WalletRepository walletRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final WalletTransactionService walletTransactionService;

    public UserProfileController(AddressService addressService, UserService userService, WalletService walletService, OrderService orderService, WalletRepository walletRepository, UserRepository userRepository, OrderRepository orderRepository, WalletTransactionService walletTransactionService) {
        this.addressService = addressService;
        this.userService = userService;
        this.walletService = walletService;
        this.orderService = orderService;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.walletTransactionService = walletTransactionService;
    }


    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        User user = null;


        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
            String email = oauthToken.getPrincipal().getAttribute("email");
            user = userService.findByEmail(email);
        } else {

            user = getCurrentUser(principal);
        }

        // Fetch all addresses for the user
        List<Address> addresses = addressService.getAllAddressesByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("addresses", addresses);
        System.out.println("Fetched addresses: " + addresses);
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
            return "add-address";
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
            return "redirect:/user/profile?error=notAuthorized";
        }

        model.addAttribute("address", address);
        return "edit-address";
    }



    @PostMapping("/address/update")
    public String updateAddress(@ModelAttribute("address") Address address, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "edit-address";
        }
        User user = getCurrentUser(principal);
        address.setUser(user);
        addressService.saveOrUpdateAddress(address);
        return "redirect:/user/profile";
    }

    @PostMapping("/address/delete/{id}")
    public String deleteAddress(@PathVariable Long id, Principal principal) {
        User user = getCurrentUser(principal);

        Address address = addressService.getAddressById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid address ID: " + id));


        if (!address.getUser().getId().equals(user.getId())) {
            return "redirect:/user/profile?error=notAuthorized";
        }

        addressService.deleteAddress(id);

        return "redirect:/user/profile";
    }

    private User getCurrentUser(Principal principal) {

        return userService.findByUsername(principal.getName());

    }


    @GetMapping("/order-history")
    public String getUserOrderHistory(Model model, Authentication authentication,
                                      @RequestParam(defaultValue = "1") int page) {
        String username = authentication.getName(); // Get the username of the logged-in user
        List<Orders> orders = orderService.getOrdersByUser(username, page);
        int totalPages = orderService.getTotalPagesByUser(username); // Pagination logic

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "order-history";
    }



    @PostMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        Orders order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            return "redirect:/user/profile/order-history?error=notAuthorized";
        }

        if (!order.getOrderStatus().equals("Delivered")) {
            orderService.cancelOrder(id);


            Wallet wallet = walletService.getWalletByUserId(user.getId());
            double refundAmount = order.getTotalAmount();
            wallet.setBalance(wallet.getBalance() + refundAmount);


            walletService.save(wallet);

            // Save transaction
            WalletTransaction transaction = new WalletTransaction();
            transaction.setWallet(wallet);
            transaction.setAmount(refundAmount);
            transaction.setDate(LocalDateTime.now());
            transaction.setDescription("Refund for Cancelled Order #" + order.getOrderItemId());
            walletTransactionService.save(transaction); // Inject this service too

            redirectAttributes.addFlashAttribute("successMessage", "Order cancelled and amount refunded to wallet.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot cancel delivered order.");
        }

        return "redirect:/user/profile/order-history";
    }


    @PostMapping("/order/return/{id}")
    public String returnOrder(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(principal);
        Orders order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to return this order.");
            return "redirect:/user/profile/order-history?error=notAuthorized";
        }

        try {
            orderService.returnOrder(id);


            Wallet wallet = walletService.getWalletByUserId(user.getId());
            double refundAmount = order.getTotalAmount();
            wallet.setBalance(wallet.getBalance() + refundAmount);
            walletService.save(wallet);


            WalletTransaction transaction = new WalletTransaction();
            transaction.setWallet(wallet);
            transaction.setAmount(refundAmount);
            transaction.setDate(LocalDateTime.now());
            transaction.setDescription("Refund for Returned Order #" + order.getOrderNumber());
            walletTransactionService.save(transaction);

            redirectAttributes.addFlashAttribute("success", "Order returned successfully. Refund has been added to your wallet.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing the return.");
        }

        return "redirect:/user/profile/order-history";
    }


    @GetMapping("/wallet")
    public String viewWallet(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Wallet wallet = walletService.getWalletByUserId(user.getId());

        List<WalletTransaction> transactions = walletTransactionService.findByWallet(wallet);
        System.out.println("Transactions count: " + wallet.getTransactions().size());

        model.addAttribute("transactions", transactions);
        model.addAttribute("wallet", wallet);

        return "wallet";
    }




}












package com.witcher.e_commerce.application.witcher.controller.user;

import com.witcher.e_commerce.application.witcher.service.EmailService;
import com.witcher.e_commerce.application.witcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("")
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            String token = userService.generateResetToken(email);
            String resetLink = "http://localhost:8080/reset-password?token=" + token;

            System.out.println("Generated Token: " + token); // Debugging

            emailService.sendEmail(email, "Password Reset", "Click here to reset your password: " + resetLink);
            model.addAttribute("message", "A password reset link has been sent to your email.");
        } catch (Exception e) {
            model.addAttribute("error", "Email address not found.");
            e.printStackTrace();  // Debugging
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        System.out.println("Received Token: " + token);  // Debugging
        boolean isValidToken = userService.validateResetToken(token);

        if (!isValidToken) {
            model.addAttribute("error", "Invalid or expired token. Please request a new one.");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("newPassword") String newPassword,
                                       Model model) {
        try {
            userService.updatePassword(token, newPassword);
            model.addAttribute("message", "Password successfully reset. You can now log in.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Error resetting password. Please try again.");
            e.printStackTrace();  // Debugging
            return "reset-password";
        }
    }



}

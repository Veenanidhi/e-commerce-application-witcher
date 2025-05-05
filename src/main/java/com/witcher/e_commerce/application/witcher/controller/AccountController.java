package com.witcher.e_commerce.application.witcher.controller;


import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.VerificationToken;
import com.witcher.e_commerce.application.witcher.service.EmailService;
import com.witcher.e_commerce.application.witcher.service.OTPService;
import com.witcher.e_commerce.application.witcher.service.UserService;
import com.witcher.e_commerce.application.witcher.service.VerificationTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Controller
@Slf4j
public class AccountController {

    private final UserService userService;

    private final OTPService otpService;

    private final EmailService emailService;

    private final VerificationTokenService verificationTokenService;

    @Autowired
    public AccountController(UserService userService, OTPService otpService, EmailService emailService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.otpService = otpService;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }



    @GetMapping("/verify-otp")
    public String showOtpVerificationPage(@RequestParam("email") String email,Model model){
        model.addAttribute("email", email);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp,
            RedirectAttributes ra
    ){
        boolean isValidOtp= otpService.verifyOTP(email, otp);
        if (isValidOtp){
            // Enable the user account after successful OTP verification
            userService.enableUser(email);

            ra.addFlashAttribute("message", "your acc has been successfully activated");
            return "redirect:/login";
        }
        else{
            ra.addFlashAttribute("error","invalid or expired otp. please try again");
            return "redirect:/verify-otp?email"+email;
        }
    }

    @GetMapping("/resend-otp")
    public String resendOtp(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        String otp = otpService.generateOTP(email);
        if (otp != null) {
            // Send the OTP to the user's email
            emailService.sendOTPEmail(email, otp);
            redirectAttributes.addFlashAttribute("message", "A new OTP has been sent to your email.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to resend OTP. Please try again later.");
        }
        return "redirect:/verify-otp?email=" + email;
    }



    @GetMapping("/activation")
    public String activation(@RequestParam("token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token);
        if (verificationToken == null) {
            model.addAttribute("message", "Your verification token is invalid");
        } else {
            User user = verificationToken.getUser();
            if (user == null) {
                model.addAttribute("error", "No user associated with this verification token");
            } else {
                if (!user.isEnabled()) {
                    LocalDate currentDate = LocalDate.now();
                    LocalDate expiryDate = verificationToken.getExpiryDate().toInstant(ZoneOffset.UTC).atZone(ZoneId.systemDefault()).toLocalDate();

                    if (currentDate.isBefore(expiryDate) || currentDate.isEqual(expiryDate)) {
                        user.setEnabled(true);
                        userService.save(user);
                        model.addAttribute("message", "Your account has been activated successfully!");
                    } else {
                        model.addAttribute("error", "Your activation link has expired. Please request a new one.");
                    }
                } else {
                    model.addAttribute("message", "Your account is already activated.");
                }
            }
        }
        return "activation";
    }







}



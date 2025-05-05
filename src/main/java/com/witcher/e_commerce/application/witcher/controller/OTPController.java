package com.witcher.e_commerce.application.witcher.controller;

import com.witcher.e_commerce.application.witcher.service.EmailService;
import com.witcher.e_commerce.application.witcher.service.OTPService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/otp")
public class OTPController {

    private final OTPService otpService;

    private final EmailService emailService;

    public OTPController(OTPService otpService, EmailService emailService) {
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateOTP(@RequestParam String email){
        String otp= otpService.generateOTP(email);
        emailService.sendOTPEmail(email, otp);
        return ResponseEntity.ok("OTP send to your email");
    }

    @PostMapping("/verify")
    public String verifyOTPWeb(@RequestParam String email, @RequestParam String otp, RedirectAttributes ra) {
        boolean isValid = otpService.verifyOTP(email, otp);
        if (isValid) {
            ra.addFlashAttribute("message", "OTP verified successfully! Please log in.");
            return "redirect:/login";
        } else {
            ra.addFlashAttribute("error", "Invalid or expired OTP.");
            return "redirect:/verify-otp?email=" + email;
        }
    }


    @PostMapping("/resend")
    public ResponseEntity<String> resendOTP(@RequestParam String email) {
        String otp = otpService.generateOTP(email);
        emailService.sendOTPEmail(email, otp);
        return ResponseEntity.ok("A new OTP has been sent to your email." );
    }



}



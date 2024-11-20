package com.witcher.e_commerce.application.witcher.controller;

import com.witcher.e_commerce.application.witcher.service.EmailService;
import com.witcher.e_commerce.application.witcher.service.OTPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp){
        boolean isValid= otpService.verifyOTP(email, otp);
        if (isValid){
            return ResponseEntity.ok("OTP verified successfully");
        }
        else {
            return ResponseEntity.status(400).body("Invalid or expired otp.");
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resendOTP(@RequestParam String email) {
        String otp = otpService.generateOTP(email);
        emailService.sendOTPEmail(email, otp);
        return ResponseEntity.ok("A new OTP has been sent to your email." );
    }



}



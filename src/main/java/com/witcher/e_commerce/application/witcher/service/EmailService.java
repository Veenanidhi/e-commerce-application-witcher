package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.dao.VerificationTokenRepository;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.VerificationToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

@Service
@Slf4j
public class EmailService {


    private final VerificationTokenRepository verificationTokenRepository;

    private final VerificationTokenService verificationTokenService;

    private final TemplateEngine templateEngine;

    private final JavaMailSender javaMailSender;


    @Autowired
    public EmailService(VerificationTokenRepository verificationTokenRepository, VerificationTokenService verificationTokenService, TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.verificationTokenService = verificationTokenService;
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }


    public void sendHtmlMail(User user) throws MessagingException {
        VerificationToken verificationToken = verificationTokenRepository.findByUser(Optional.ofNullable(user));
        //check if the user has a token
        if (verificationToken != null) {
            log.info("user verification token {}", verificationToken.getToken());
            String token = verificationToken.getToken();
            Context context = new Context();
            context.setVariable("title", "Verify your email address");
            System.out.println("goinggggggggg.........");
            context.setVariable("link", "http://localhost:8080/activation?token=" + token);

            //create an html template and pass the variables to it
            String body = templateEngine.process("verification", context);

            //snd verification email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("email verification");
            helper.setText(body, true);
            javaMailSender.send(message);
        }
    }


    public void sendOTPEmail(String email, String otp){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("YOUR OTP CODE");
        message.setText("Your OTP code is " + otp + ". This code will expire in 5 minutes.");
        javaMailSender.send(message);
    }

    // Resend OTP Functionality
    public String resendOTP(String email) {
        log.info("Resending OTP to email: {}", email);

        // Check if the user already has an OTP in the repository
        VerificationToken existingToken = verificationTokenRepository.findByEmail(email);

        if (existingToken != null && !existingToken.isExpired()) {
            log.info("Existing OTP for email: {}", email);
            String otp = existingToken.getToken();

            // Resend the existing OTP
            sendOTPEmail(email, otp);
            return "OTP resent successfully to: " + email;
        } else {
            // Generate a new OTP if none exists or if the old one has expired
            log.info("Generating a new OTP for email: {}", email);
            String newOtp = verificationTokenService.generateNewOTP(email);

            // Save the new OTP in the database
            VerificationToken newToken = verificationTokenService.createVerificationToken(email, newOtp);
            verificationTokenRepository.save(newToken);

            // Send the new OTP
            sendOTPEmail(email, newOtp);
            return "New OTP sent successfully to: " + email;
        }
    }

    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }



}









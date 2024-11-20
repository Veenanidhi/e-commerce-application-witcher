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
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
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


//    public void sendOTPEmail(String email, String otp) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Secure One-Time Password (OTP) for Your Account");
//
//        String emailBody =
//                "Dear Valued User,\n\n" +
//                        "As requested, here is your One-Time Password (OTP) for account verification:\n\n" +
//                        "OTP: " + otp       + "\n\n" +
//                        "Please note:\n" +
//                        "- This OTP is valid for 5 minutes from the time of this email.\n" +
//                        "- For security reasons, do not share this OTP with anyone.\n" +
//                        "- If you did not request this OTP, please contact our support team immediately.\n\n" +
//                        "If you have any questions or concerns, please don't hesitate to reach out to our customer support.\n\n" +
//                        "Best regards,\n" +
//                        "Your Security Team";
//
//        message.setText(emailBody);
//        javaMailSender.send(message);
//    }
}
package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.dao.VerificationTokenRepository;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;


    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }


    @Transactional
    public VerificationToken findByToken(String token){
        return verificationTokenRepository.findByToken(token);
    }

    @Transactional
    public VerificationToken findByUser(User user){
        return verificationTokenRepository.findByUser(Optional.ofNullable(user));
    }

    @Transactional
    public void save(User user, String token){
        VerificationToken verificationToken= new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

    public String generateNewOTP(String email) {
        return String.format("%06d", new Random().nextInt(999999)); // Generate a random 6-digit OTP
    }

    // Create a new VerificationToken for the given email and OTP
    @Transactional
    public VerificationToken createVerificationToken(String email, String otp) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setEmail(email);
        verificationToken.setToken(otp);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes
        return verificationTokenRepository.save(verificationToken);
    }

    // Check if a token is expired
    public boolean isTokenExpired(VerificationToken token) {
        return LocalDateTime.now().isAfter(token.getExpiryDate());
    }






}




// To calculate expiry date
 /* public void setExpiry(User user, String token) {
        VerificationToken verificationToken= new VerificationToken(token, user);

        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
        verificationToken.setExpiryDate(expiryDate);
        verificationTokenRepository.save(verificationToken);
    }
*/

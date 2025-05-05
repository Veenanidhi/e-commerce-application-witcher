package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.dao.OtpRepository;
import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import com.witcher.e_commerce.application.witcher.entity.OtpEntity;
import com.witcher.e_commerce.application.witcher.entity.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {

    private static final int OTP_LENGTH = 6;

    private static final int OTP_VALIDITY_DURATION = 5; //in minutes

    private final OtpRepository otpRepository;

    private final UserRepository userRepository;

    public OTPService(OtpRepository otpRepository, UserRepository userRepository) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
    }

//    public String generateOTP(String email) {
//        String otp = generateRandomOTP();
//        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(OTP_VALIDITY_DURATION);
//
//        OtpEntity otpEntity = new OtpEntity(email, otp, expirationTime);
//        otpRepository.save(otpEntity);
//
//        return otp + ";" + expirationTime; // Send OTP and expiration time
//    }

    // Method to generate a new OTP for a user
    public String generateOTP(String email) {
        String otp = generateRandomOTP();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(OTP_VALIDITY_DURATION);

        // Create or update the OTP entity for the given email
        Optional<OtpEntity> existingOtpEntity = otpRepository.findByEmail(email);
        if (existingOtpEntity.isPresent()) {
            OtpEntity otpEntity = existingOtpEntity.get();
            otpEntity.setOtp(otp);
            otpEntity.setExpirationTime(expirationTime);
            otpRepository.save(otpEntity);
        } else {
            OtpEntity newOtpEntity = new OtpEntity(email, otp, expirationTime);
            otpRepository.save(newOtpEntity);
        }

        return otp;
    }



    private String generateRandomOTP() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }


    public boolean verifyOTP(String email, String otp) {
        Optional<OtpEntity> otpEntityOptional = otpRepository.findByEmail(email);
        if (otpEntityOptional.isPresent()) {
            OtpEntity otpEntity = otpEntityOptional.get();

            if (isOtpExpired(otpEntity)) {
                otpRepository.delete(otpEntity);
                return false;
            }

            boolean isValid = otpEntity.getOtp().equals(otp);
            if (isValid) {
                otpRepository.delete(otpEntity);

                // Fetch the user and enable the account
                Optional<User> userOptional = userRepository.findByEmail(email);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    user.setEnabled(true);
                    userRepository.save(user);
                }
            }

            return isValid;
        }
        return false;
    }


    private boolean isOtpExpired(OtpEntity otpEntity) {
        return otpEntity.getExpirationTime().isBefore(LocalDateTime.now());
    }

    public long getRemainingTime(String email) {
        Optional<OtpEntity> otpEntityOptional = otpRepository.findByEmail(email);
        if (otpEntityOptional.isPresent()) {
            OtpEntity otpEntity = otpEntityOptional.get();
            if (!isOtpExpired(otpEntity)) {
                return Duration.between(LocalDateTime.now(), otpEntity.getExpirationTime()).getSeconds();
            }
        }
        return 0;
    }



}

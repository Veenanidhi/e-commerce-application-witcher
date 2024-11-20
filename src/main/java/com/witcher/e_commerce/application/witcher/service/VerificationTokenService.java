package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.dao.VerificationTokenRepository;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return verificationTokenRepository.findByUser(user);
    }
    @Transactional
    public void save(User user, String token){
        VerificationToken verificationToken= new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
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

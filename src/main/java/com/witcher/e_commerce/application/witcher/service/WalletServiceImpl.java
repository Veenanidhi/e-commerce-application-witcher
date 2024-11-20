package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.dao.UserRepository;
import com.witcher.e_commerce.application.witcher.dao.WalletRepository;
import com.witcher.e_commerce.application.witcher.entity.Transaction;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService{

    @Autowired
    private  WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Wallet createWallet(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user ID: " + userId));

    }

    @Override
    public Wallet updateBalance(Long walletId, Double amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet setReferralUsed(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setReferralUsed(true);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet addTransaction(Long walletId, Transaction transaction) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        transaction.setWallet(wallet);
        wallet.getTransactions().add(transaction);
        walletRepository.save(wallet);
        return wallet;
    }

    @Override
    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }
}

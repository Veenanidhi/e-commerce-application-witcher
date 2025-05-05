package com.witcher.e_commerce.application.witcher.service.wallet;

import com.witcher.e_commerce.application.witcher.entity.Transaction;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.Wallet;

import java.util.List;

public interface WalletService {
    Wallet getWalletByUserId(Long id);

    Wallet createWallet(Long userId);

    Wallet updateBalance(Long walletId, Double amount);

    Wallet setReferralUsed(Long walletId);

    Wallet save(Wallet wallet);

    void updateBalance(Wallet wallet);
    
    /// newwwwwwwwwwwwwwwwwwwwwww

    Transaction addFunds(User user, Double amount, String paymentMethod);

    Transaction deductFunds(User user, Double amount, String paymentMethod);

    List<Transaction> getWalletTransactions(User user);

    Double getWalletBalance(User user);

    void deductFromWallet(User user, Double totalAmount);
}

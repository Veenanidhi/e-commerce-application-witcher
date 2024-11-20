package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.entity.Transaction;
import com.witcher.e_commerce.application.witcher.entity.Wallet;

public interface WalletService {
    Wallet createWallet(Long userId);

    Wallet getWalletByUserId(Long userId);

    Wallet updateBalance(Long walletId, Double amount);

    Wallet setReferralUsed(Long walletId);

    Wallet addTransaction(Long walletId, Transaction transaction);

    Wallet save(Wallet wallet);
}

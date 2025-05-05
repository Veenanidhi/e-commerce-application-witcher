package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.entity.Wallet;
import com.witcher.e_commerce.application.witcher.entity.WalletTransaction;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionService {

    void save(WalletTransaction transaction);

    List<WalletTransaction> findByWallet(Wallet wallet);

    Object getAllTransactions();

    Optional<WalletTransaction> getTransactionById(Long id);
}

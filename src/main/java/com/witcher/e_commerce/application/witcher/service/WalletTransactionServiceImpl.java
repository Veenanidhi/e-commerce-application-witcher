package com.witcher.e_commerce.application.witcher.service;

import com.witcher.e_commerce.application.witcher.dao.WalletTransactionRepository;
import com.witcher.e_commerce.application.witcher.entity.Wallet;
import com.witcher.e_commerce.application.witcher.entity.WalletTransaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService{

    private final WalletTransactionRepository walletTransactionRepository;

    public WalletTransactionServiceImpl(WalletTransactionRepository walletTransactionRepository) {
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Override
    public void save(WalletTransaction transaction) {
        walletTransactionRepository.save(transaction);
    }

    @Override
    public List<WalletTransaction> findByWallet(Wallet wallet) {
        return walletTransactionRepository.findByWalletOrderByDateDesc(wallet);
    }

    //admin side

    @Override
    public Object getAllTransactions() {
        return walletTransactionRepository.findAll();
    }

    @Override
    public Optional<WalletTransaction> getTransactionById(Long id) {
        return walletTransactionRepository.findById(id);
    }


    public List<WalletTransaction> getUserTransactions(Long userId) {
        return walletTransactionRepository.findByWallet_User_Id(userId);
    }
}

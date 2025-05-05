package com.witcher.e_commerce.application.witcher.service.wallet;

import com.witcher.e_commerce.application.witcher.dao.TransactionRepository;
import com.witcher.e_commerce.application.witcher.dao.WalletRepository;
import com.witcher.e_commerce.application.witcher.entity.Transaction;
import com.witcher.e_commerce.application.witcher.entity.User;
import com.witcher.e_commerce.application.witcher.entity.Wallet;
import com.witcher.e_commerce.application.witcher.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService{

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    public Wallet getWalletByUserId(Long id) {
        return walletRepository.findByUserId(id)
                .orElseGet(() -> createAndSaveNewWallet(id));
    }

    @Override
    public Wallet createWallet(Long userId) {
        return createAndSaveNewWallet(userId);
    }

    private Wallet createAndSaveNewWallet(Long userId) {
        User user = userService.findById(userId);
        Wallet newWallet = new Wallet();
        newWallet.setUser(user);
        newWallet.setBalance(0.0);
        return walletRepository.save(newWallet);
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
    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Override
    public void updateBalance(Wallet wallet) {
        walletRepository.save(wallet);
    }

    public Transaction addFunds(User user, Double amount, String paymentMethod) {
        Wallet wallet = getWalletByUserId(user.getId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(0.0);
            wallet = walletRepository.save(wallet);
        }

        // Update Wallet Balance
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        // Save Transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("CREDIT");
        transaction.setDescription("Added funds via " + paymentMethod);
        transaction.setDate(new Date());
        transactionRepository.save(transaction);

        return transaction;
    }

    public  Transaction deductFunds(User user, Double amount, String paymentMethod) {
        Wallet wallet = getWalletByUserId(user.getId());
        if (wallet == null || wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct from Wallet
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        // Save Transaction
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("DEBIT");
        transaction.setDescription("Deducted funds for " + paymentMethod);
        transaction.setDate(new Date());
        transactionRepository.save(transaction);

        return transaction;
    }

    public List<Transaction> getWalletTransactions(User user) {
        Wallet wallet = getWalletByUserId(user.getId());
        return transactionRepository.findByWallet(wallet, Sort.by(Sort.Direction.DESC, "transactionDate"));
    }

    @Override
    public Double getWalletBalance(User user) {
        return walletRepository.findByUserId(user.getId())
                .map(Wallet::getBalance)
                .orElse(0.0);
    }

    @Override
    @Transactional
    public void deductFromWallet(User user, Double amount) {
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found for user"));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);
    }



}


package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long walletId;

    private Double balance= 0.0;

    @OneToOne
    @JoinColumn(name = "id")
    private User user;


    // One Wallet can have many Transactions
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    private boolean referralUsed= false;

    public Wallet() {
    }

    public Wallet(long walletId, Double balance, User user, List<Transaction> transactions, boolean referralUsed) {
        this.walletId = walletId;
        this.balance = balance;
        this.user = user;
        this.transactions = transactions;
        this.referralUsed = referralUsed;
    }

    public Wallet(long walletId, Double balance, User user, boolean referralUsed) {
        this.walletId = walletId;
        this.balance = balance;
        this.user = user;
        this.referralUsed = referralUsed;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long walletId) {
        this.walletId = walletId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isReferralUsed() {
        return referralUsed;
    }

    public void setReferralUsed(boolean referralUsed) {
        this.referralUsed = referralUsed;
    }

}


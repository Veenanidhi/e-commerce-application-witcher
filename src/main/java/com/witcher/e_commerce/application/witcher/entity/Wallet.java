package com.witcher.e_commerce.application.witcher.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long walletId;

    private Double balance= 0.0;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id" ,nullable = false)
    @JsonBackReference
    private User user;


    // One Wallet can have many Transactions
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<WalletTransaction> transactions = new ArrayList<>();

    private boolean referralUsed= false;

    public Wallet() {
    }

    public Wallet(long walletId, Double balance, User user, List<WalletTransaction> transactions, boolean referralUsed) {
        this.walletId = walletId;
        this.balance = balance;
        this.user = user;
        this.transactions = transactions;
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

    public List<WalletTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<WalletTransaction> transactions) {
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


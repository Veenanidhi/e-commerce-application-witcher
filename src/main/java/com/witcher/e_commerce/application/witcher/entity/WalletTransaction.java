package com.witcher.e_commerce.application.witcher.entity;

import com.witcher.e_commerce.application.witcher.service.PaymentMethod;
import com.witcher.e_commerce.application.witcher.service.TransactionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class WalletTransaction {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private double amount;

        private String description;

        private LocalDateTime date;

        @ManyToOne
        @JoinColumn(name = "wallet_id")
        private Wallet wallet;


    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;





    public WalletTransaction() {

    }

    public WalletTransaction(Long id, double amount, String description, LocalDateTime date, Wallet wallet) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.wallet = wallet;

    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}



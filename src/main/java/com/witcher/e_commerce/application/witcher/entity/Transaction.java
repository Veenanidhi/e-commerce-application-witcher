package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;

@Entity
public class Transaction {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long transactionId;

   @ManyToOne
   @JoinColumn(name = "wallet_id")
   private Wallet wallet;

   private Double amount;

   private String transactionType;

   private String description;

   public Transaction() {
   }

   public Transaction(Long transactionId, Wallet wallet, Double amount, String transactionType, String description) {
      this.transactionId = transactionId;
      this.wallet = wallet;
      this.amount = amount;
      this.transactionType = transactionType;
      this.description = description;
   }

   public Long getTransactionId() {
      return transactionId;
   }

   public void setTransactionId(Long transactionId) {
      this.transactionId = transactionId;
   }

   public Wallet getWallet() {
      return wallet;
   }

   public void setWallet(Wallet wallet) {
      this.wallet = wallet;
   }

   public Double getAmount() {
      return amount;
   }

   public void setAmount(Double amount) {
      this.amount = amount;
   }

   public String getTransactionType() {
      return transactionType;
   }

   public void setTransactionType(String transactionType) {
      this.transactionType = transactionType;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }



}

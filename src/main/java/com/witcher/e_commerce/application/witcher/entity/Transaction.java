package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;

import java.util.Date;

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

   @Temporal(TemporalType.TIMESTAMP)
   private Date date;

   @ManyToOne
   @JoinColumn(name = "user_id")
   private User user;


   public Transaction() {
   }

   public Transaction(Long transactionId, Wallet wallet, Double amount, String transactionType, String description, Date date, User user) {
      this.transactionId = transactionId;
      this.wallet = wallet;
      this.amount = amount;
      this.transactionType = transactionType;
      this.description = description;
      this.date = date;
      this.user = user;
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

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}

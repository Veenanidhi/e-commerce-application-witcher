package com.witcher.e_commerce.application.witcher.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name ="verification_token")
@Data
@Getter
@Setter
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String email;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @OneToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name ="id", referencedColumnName = "id")
    private User user;


    public VerificationToken() {
       // this.expiryDate = LocalDateTime.now().plusDays(1);
    }

    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusDays(1);  // Set expiry to 1 day from now
    }

    public VerificationToken(String token, Optional<User> user) {
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
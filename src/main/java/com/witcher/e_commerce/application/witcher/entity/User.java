package com.witcher.e_commerce.application.witcher.entity;

import com.witcher.e_commerce.application.witcher.service.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Getter
    @Column(name = "username")
    @NotNull(message = "is required")
    private String username;


    @Column(name = "email")
    @NotNull(message = "is required")
    private String email;

    @Getter
    @Column
    @NotNull(message = "is required")
    private String password;

    @Column(name = "phone_no")
    private String phone_no;


    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Orders> orders;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_coupon",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id"))
    private Set<Coupon> coupons = new HashSet<>();




    @Column(nullable = false)
    private boolean isEnabled = false;

    public Role setRole(Role role) {
        this.role = role;
        return role;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public User() {
    }

    public User(Long id, String username, String email, String password, String phone_no, Role role, List<Address> addresses, List<Orders> orders, Set<Coupon> coupons, boolean isEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone_no = phone_no;
        this.role = role;
        this.addresses = addresses;
        this.orders = orders;
        this.coupons = coupons;
        this.isEnabled = isEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public Role getRole() {
        return role;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }


    public boolean isEnabled() {
        return isEnabled;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Set<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(Set<Coupon> coupons) {
        this.coupons = coupons;
    }
}



package com.witcher.e_commerce.application.witcher.dto;

import com.witcher.e_commerce.application.witcher.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class AddressDTO {
    private Long address_id;
    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;

    private String firstName;

    private String lastName;

    private String city;

    private String country;

    private String state;

    private String pinCode;

    private String number;
}

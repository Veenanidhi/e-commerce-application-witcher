package com.witcher.e_commerce.application.witcher.service.address;

import com.witcher.e_commerce.application.witcher.entity.Address;
import com.witcher.e_commerce.application.witcher.entity.User;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface AddressService {

    List<Address> getAllAddressesByUser(User user);

    Address saveOrUpdateAddress(Address address);

    Optional<Address> getAddressById(Long id);

    void deleteAddress(Long id);
}

package com.witcher.e_commerce.application.witcher.dao;

import com.witcher.e_commerce.application.witcher.entity.CategoryOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryOfferRepository extends JpaRepository<CategoryOffer, Long> {


}

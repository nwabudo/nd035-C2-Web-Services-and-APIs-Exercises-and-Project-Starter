package com.udacity.pricing.repository;

import com.udacity.pricing.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    @RestResource(path = "byVehicleId")
    Price findByVehicleId(@Param("vehicleId") Long vehicleId);

    @Override
    @RestResource(exported = false)
    void deleteById(Long id);
}

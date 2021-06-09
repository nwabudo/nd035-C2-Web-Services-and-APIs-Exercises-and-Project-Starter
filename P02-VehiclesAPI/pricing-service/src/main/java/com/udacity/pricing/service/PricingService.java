package com.udacity.pricing.service;

import com.udacity.pricing.entity.Price;
import com.udacity.pricing.exceptions.PriceException;

/**
 * Implements the pricing service to get prices for each vehicle.
 */
public interface PricingService {



    /**
     * If a valid vehicle ID, gets the price of the vehicle from the stored array.
     * @param vehicleId ID number of the vehicle the price is requested for.
     * @return price of the requested vehicle
     * @throws PriceException vehicleID was not found
     */
    Price getPrice(Long vehicleId) throws PriceException;



}

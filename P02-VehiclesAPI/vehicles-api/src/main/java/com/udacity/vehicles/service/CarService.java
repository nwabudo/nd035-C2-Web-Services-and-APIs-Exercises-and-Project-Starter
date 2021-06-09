package com.udacity.vehicles.service;

import com.udacity.vehicles.domain.car.Car;
import java.util.List;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
public interface CarService {

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    List<Car> list();

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    Car findById(Long id);

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    Car save(Car car);

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    void delete(Long id);
}

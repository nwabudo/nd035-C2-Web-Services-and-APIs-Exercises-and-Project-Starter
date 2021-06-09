package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import com.udacity.vehicles.exception.CarNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    private final PriceClient priceClient;

    private final MapsClient mapsClient;

    public CarServiceImpl(CarRepository carRepository, PriceClient priceClient, MapsClient mapsClient) {
        /**
         * TODO: Add the Maps and Pricing Web Clients you create
         *   in `VehiclesApiApplication` as arguments and set them here.
         */
        this.carRepository = carRepository;
        this.priceClient = priceClient;
        this.mapsClient = mapsClient;
    }

    @Override
    public List<Car> list() {
        return carRepository.findAll();
    }

    public Car findById(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *   Remove the below code as part of your implementation.
         */
        Optional<Car> carOptional = this.carRepository.findById(id);
        Car car = carOptional.orElseThrow(() -> new CarNotFoundException("Car with id: " + id + " not found"));
        /**
         * TODO: Use the Pricing Web client you create in `VehiclesApiApplication`
         *   to get the price based on the `id` input'
         * TODO: Set the price of the car
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
        String price = priceClient.getPrice(car.getId());
        car.setPrice(price);
        /**
         * TODO: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */
        Location location = mapsClient.getAddress(car.getLocation());
        car.setLocation(location);
        return car;
    }

    public Car save(Car car) {
        if (car.getId() != null) {
            return carRepository.findById(car.getId())
                .map(carToBeUpdated -> {
                 carToBeUpdated.setDetails(car.getDetails());
                 carToBeUpdated.setLocation(car.getLocation());
                 carToBeUpdated.setCondition(car.getCondition());
                 return carRepository.save(carToBeUpdated);
             }).orElseThrow(CarNotFoundException::new);
        }
        return carRepository.save(car);
    }

    public void delete(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         */
        Optional<Car> carOptional = this.carRepository.findById(id);
        Car car = carOptional.orElseThrow(() -> new CarNotFoundException("Car with id: " + id + " not found"));
        /**
         * TODO: Delete the car from the repository.
         */
        this.carRepository.delete(car);
        this.priceClient.assignRandomPrice(id);
    }
}

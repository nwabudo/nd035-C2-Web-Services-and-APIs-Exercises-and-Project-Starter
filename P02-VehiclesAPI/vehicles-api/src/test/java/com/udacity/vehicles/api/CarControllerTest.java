package com.udacity.vehicles.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JacksonTester<Car> json;

	@MockBean
	private CarService carService;

	//@MockBean
	//private CarResourceAssembler carResourceAssembler;

	@MockBean
	private PriceClient priceClient;

	@MockBean
	private MapsClient mapsClient;

	/**
	 * Creates pre-requisites for testing, such as an example car.
	 */
	@Before
	public void setup() {
		Car car = getCar();
		car.setId(1L);
		given(carService.save(any())).willReturn(car);
		given(carService.findById(any())).willReturn(car);
		given(carService.list()).willReturn(Collections.singletonList(car));
	}

	/**
	 * Tests for successful creation of new car in the system
	 * 
	 * @throws Exception when car creation fails in the system
	 */
	@Test
	public void createCar() throws Exception {
		Car car = getCar();
		mvc.perform(post(new URI("/cars"))
				.with(userTester())
				.content(json.write(car).getJson())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8))
		   .andExpect(status().isCreated());
	}

	@Test
	public void createCarToFail() throws Exception {
		Car car = getCar();
		car.setCondition(null);

		mvc.perform(post(new URI("/cars"))
				.with(userTester())
				.content(json.write(car).getJson())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8))
		   .andExpect(status().isBadRequest())
		   .andExpect(jsonPath("$.message").value("Validation failed"))
		   .andExpect(jsonPath("$.errors[*]").exists())
		   .andDo(print());
	}
	
	/**
	 * Tests if the write operation appropriately returns an updated Car.
	 * 
	 * @throws Exception if the update operation of the vehicle fails
	 */
	@Test
	public void updateCar() throws Exception {
		
		Car car = getCar();
		/*car.setCondition(Condition.NEW);
		Manufacturer manufacturer = new Manufacturer(103, "BMW");
		car.getDetails().setManufacturer(manufacturer);*/
		
		mvc.perform(put("/cars/{id}", 1)
				.with(userTester())
				.content(json.write(car).getJson())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8))
		   .andExpect(status().isOk())
		   .andExpect(jsonPath("$.details.manufacturer.code").isNotEmpty())
		   .andExpect(jsonPath("$.condition").value(car.getCondition().name()))
		   .andExpect(jsonPath("$.details.manufacturer.code").value(car.getDetails().getManufacturer().getCode()))
		   .andExpect(jsonPath("$.details.manufacturer.name").value(car.getDetails().getManufacturer().getName()))
		   .andDo(print());
	}

	/**
	 * Tests if the read operation appropriately returns a list of vehicles.
	 * 
	 * @throws Exception if the read operation of the vehicle list fails
	 */
	@Test
	public void listCars() throws Exception {
		/**
		 * TODO: Add a test to check that the `get` method works by calling the whole
		 * list of vehicles. This should utilize the car from `getCar()` below (the
		 * vehicle will be the first in the list).
		 */
		Car car = getCar();

		mvc.perform(get(new URI("/cars"))
					  .with(userTester()))
				   .andDo(print()).andExpect(status().isOk())
				   .andExpect(jsonPath("$._embedded.carList").exists())
				   .andExpect(jsonPath("$._embedded.carList[*].id").isNotEmpty())
				   .andExpect(jsonPath("$._embedded.carList[0].condition").value(car.getCondition().name()))
				   .andExpect(jsonPath("$._embedded.carList[0].details.body").value(car.getDetails().getBody()));
	}

	/**
	 * Tests the read operation for a single car by ID.
	 * 
	 * @throws Exception if the read operation for a single car fails
	 */
	@Test
	public void findCar() throws Exception {
		/**
		 * TODO: Add a test to check that the `get` method works by calling a vehicle by
		 * ID. This should utilize the car from `getCar()` below.
		 */
		Car car = getCar();

		mvc.perform(get("/cars/{id}", 1)
				.with(userTester()))
		   .andExpect(status().isOk())
		   .andExpect(jsonPath("$.id").exists())
		   .andExpect(jsonPath("$.details.manufacturer.code").isNotEmpty())
		   .andExpect(jsonPath("$.condition").value(car.getCondition().name()))
		   .andExpect(jsonPath("$.details.body").value(car.getDetails().getBody()));
	}

	/**
	 * Tests the deletion of a single car by ID.
	 * 
	 * @throws Exception if the delete operation of a vehicle fails
	 */
	@Test
	public void deleteCar() throws Exception {
		/**
		 * TODO: Add a test to check whether a vehicle is appropriately deleted when the
		 * `delete` method is called from the Car Controller. This should utilize the
		 * car from `getCar()` below.
		 */
		mvc.perform(delete("/cars/{id}", 1)
				.with(userTester()))
		   .andDo(print()).andExpect(status().isNoContent());
	}

	/**
	 * Creates an example Car object for use in testing.
	 * 
	 * @return an example Car object
	 */
	private Car getCar() {
		Car car = new Car();
		car.setLocation(new Location(40.730610, -73.935242));
		Details details = new Details();
		Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
		details.setManufacturer(manufacturer);
		details.setModel("Impala");
		details.setMileage(32280);
		details.setExternalColor("white");
		details.setBody("sedan");
		details.setEngine("3.6L V6");
		details.setFuelType("Gasoline");
		details.setModelYear(2018);
		details.setProductionYear(2018);
		details.setNumberOfDoors(4);
		car.setDetails(details);
		car.setCondition(Condition.USED);
		return car;
	}

	/*
	 * private static <T> T fromJSON(final TypeReference<T> type, final String
	 * jsonPacket) { T data = null; try { data = new
	 * ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
	 * false) .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
	 * .readValue(jsonPacket, type); } catch (Exception e) {
	 * System.out.println("Error in parsing Object " + e.getMessage()); } return
	 * data; }
	 */
	public static RequestPostProcessor userTester() {
		return user("user").password("password").roles("ADMIN");
	}
}
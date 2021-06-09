package com.udacity.pricing.controller;

import com.udacity.pricing.entity.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllPrices() {
        String url = "http://localhost:" + this.port;

        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/prices")
                                      .queryParam("prices", 1)
                                      .queryParam("size", "5")
                                      .queryParam("sort", "asc").build().toUri();

        ResponseEntity<Object> response =
                this.restTemplate.withBasicAuth("user", "password")
                                 .getForEntity(uri, Object.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void getPriceById() {
        String url = "http://localhost:" + this.port;

        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/prices/3")
                                      .build().toUri();

        ResponseEntity<Price> response =
                this.restTemplate.withBasicAuth("user", "password")
                                 .getForEntity(uri, Price.class);
        Price price = response.getBody();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Not Equal to null", price != null);
    }
}

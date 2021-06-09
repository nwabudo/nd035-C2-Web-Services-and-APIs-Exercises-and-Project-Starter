package com.udacity.pricing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Price not found")
public class PriceException extends RuntimeException {

    public PriceException(String message) {
        super(message);
    }
}

package com.tuandatcoder.trekkerbackend.exception.Token;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TokenAPIExceptionHandler {

    @ExceptionHandler(UnableToExtractTokenForReturnOrder.class)
    public ResponseEntity<?> unableToExtractTokenForReturnOrder(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
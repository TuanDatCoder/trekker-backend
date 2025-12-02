package com.tuandatcoder.trekkerbackend.exception;

public class AlreadyExistedException extends RuntimeException{
    public AlreadyExistedException(String message) {
        super(message);
    }

}

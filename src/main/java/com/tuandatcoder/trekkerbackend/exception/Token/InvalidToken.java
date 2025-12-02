package com.tuandatcoder.trekkerbackend.exception.Token;

public class InvalidToken extends RuntimeException{
    public InvalidToken(String message) {
        super(message);
    }
}

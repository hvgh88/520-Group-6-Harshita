package com.umass.hangout.exception;

public class InvalidDateTimeFormatException extends RuntimeException {
    public InvalidDateTimeFormatException(String message) {
        super(message);
    }
}
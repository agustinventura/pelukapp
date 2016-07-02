package com.spanishcoders.model.exceptions;

/**
 * Created by agustin on 29/06/16.
 */
public class TimeTableNotFoundException extends RuntimeException {
    public TimeTableNotFoundException(String message) {
        super(message);
    }
}

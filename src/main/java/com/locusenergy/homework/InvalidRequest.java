package com.locusenergy.homework;

/**
 * Should be called InvalidRequestException.
 */
public class InvalidRequest extends RuntimeException {
    public InvalidRequest(String s) {
        super(s);
    }
}

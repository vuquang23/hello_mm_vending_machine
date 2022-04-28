package com.hellomm.common.exceptions;

public class InvalidDenominationException extends Exception {
    private String message;

    public InvalidDenominationException(String message) {
        super();
        this.message = message;
    }

    public InvalidDenominationException() {
        super();
        this.message = "Invalid denomination. Try again:";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

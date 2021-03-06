package com.hellomm.common.exceptions;

public class CannotPayBackException extends Exception {
    private String message;

    public CannotPayBackException(String message) {
        super();
        this.message = message;
    }

    public CannotPayBackException() {
        super();
        this.message = "Cannot return cash for customer";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

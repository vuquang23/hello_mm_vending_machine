package com.hellomm.common.exceptions;

public class InvalidProductException extends Exception {
    private String message;

    public InvalidProductException(String message) {
        super();
        this.message = message;
    }

    public InvalidProductException() {
        super();
        this.message = "Product name is invalid";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

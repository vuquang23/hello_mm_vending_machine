package com.hellomm.common.exceptions;

public class CannotUnselectProductException extends Exception {
    private String message;

    public CannotUnselectProductException(String message) {
        super();
        this.message = message;
    }

    public CannotUnselectProductException() {
        super();
        this.message = "Number of this product in cart is 0.";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

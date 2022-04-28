package com.hellomm.common.exceptions;

public class NotEnoughPaidException extends Exception {
    private String message;

    public NotEnoughPaidException(String message) {
        super();
        this.message = message;
    }

    public NotEnoughPaidException() {
        super();
        this.message = "Not enough cash to pay transaction.";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

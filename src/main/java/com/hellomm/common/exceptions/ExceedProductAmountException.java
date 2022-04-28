package com.hellomm.common.exceptions;

public class ExceedProductAmountException extends Exception {
    private String message;

    public ExceedProductAmountException(String message) {
        super();
        this.message = message;
    }

    public ExceedProductAmountException() {
        super();
        this.message = "Exceed product amount.";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

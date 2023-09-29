package com.charter.rewards.exception;

public class InvalidDateRangeException extends RuntimeException {
    private String message;

    public InvalidDateRangeException() {
    }

    public InvalidDateRangeException(String msg) {
        super(msg);
        this.message = msg;
    }
}

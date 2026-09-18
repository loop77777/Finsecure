package com.finsecure.exception;

public class LedgerCapacityExceededException extends RuntimeException {

    public LedgerCapacityExceededException(String message) {
        super(message);
    }
}

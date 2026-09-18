package com.finsecure.exception;

/**
 * Checked Exception: Raised when payload syntax or protocol rules are violated.
 */
public class InvalidPayloadException extends Exception {
    public InvalidPayloadException(String message) {
        super(message);
    }
}

package com.finsecure.exception;

/**
 * Unchecked Exception: Raised when hardware/buffer limits are breached.
 */
public class LedgerCapacityExceededException extends RuntimeException {
    public LedgerCapacityExceededException(String message) {
        super(message);
    }
}
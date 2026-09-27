package com.exception;

public class SubscriptionShieldException extends RuntimeException {
    public SubscriptionShieldException(String message) {
        super(message);
    }

    public SubscriptionShieldException(String message, Throwable cause) {
        super(message, cause);
    }
}

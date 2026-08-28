package com.reactive.product.exception;

public class InsufficientProductQuantityException extends RuntimeException {

    public InsufficientProductQuantityException(String message) {
        super(message);
    }
}
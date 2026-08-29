package com.reactive.product.exception;

import com.reactive.product.model.entity.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleProductNotFound(
            ProductNotFoundException exception) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                new ApiResponse<>(
                                        exception.getMessage(),
                                        null
                                )
                        )
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<List<String>>>> handleValidationException(
            WebExchangeBindException exception) {

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage()
                )
                .toList();

        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ApiResponse<>(
                                        "Validation failed",
                                        errors
                                )
                        )
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGenericException(
            Exception exception) {

        System.err.println(
                "Unexpected error: " + exception.getMessage()
        );

        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                new ApiResponse<>(
                                        "An unexpected error occurred",
                                        null
                                )
                        )
        );
    }

    @ExceptionHandler(InsufficientProductQuantityException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInsufficientQuantity(
            InsufficientProductQuantityException exception) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ApiResponse<>(
                                        exception.getMessage(),
                                        null
                                )
                        )
        );
    }

    @ExceptionHandler(ProductOperationException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleProductOperation(
            ProductOperationException exception) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ApiResponse<>(
                                        exception.getMessage(),
                                        null
                                )
                        )
        );
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleProductAlreadyExists(
            ProductAlreadyExistsException ex) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(
                                new ApiResponse<>(
                                        ex.getMessage(),
                                        null
                                )
                        )
        );
    }
}
package com.reactive.product.controller;

import com.reactive.product.model.entity.request.ProductRequest;
import com.reactive.product.model.entity.request.StockUpdateRequest;
import com.reactive.product.model.entity.response.ApiResponse;
import com.reactive.product.model.entity.response.ProductResponse;
import com.reactive.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request)
                .map(response ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                        new ApiResponse<>(
                                                "Product created successfully",
                                                response
                                        )
                                )
                );
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<ProductResponse>>>> getAllProducts() {

        return productService.getAllProducts()
                .collectList()
                .map(products ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Products fetched successfully",
                                        products
                                )
                        )
                );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> getProductById(
            @PathVariable Long id) {

        return productService.getProductById(id)
                .map(response ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Product fetched successfully",
                                        response
                                )
                        )
                );
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return productService.updateProduct(id, request)
                .map(response ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Product updated successfully",
                                        response
                                )
                        )
                );
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteProduct(
            @PathVariable Long id) {

        return productService.deleteProduct(id)
                .thenReturn(
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Product deleted successfully",
                                        null
                                )
                        )
                );
    }

    @PatchMapping("/{id}/activate")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> activateProduct(
            @PathVariable Long id) {

        return productService.activateProduct(id)
                .map(response ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Product activated successfully",
                                        response
                                )
                        )
                );
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> deactivateProduct(
            @PathVariable Long id) {

        return productService.deactivateProduct(id)
                .map(response ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Product deactivated successfully",
                                        response
                                )
                        )
                );
    }

    @PatchMapping("/{id}/stock")
    public Mono<ProductResponse> addStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {

        return productService.addStock(id, request);
    }


    @PatchMapping("/{id}/reserve/{quantity}")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> reserveProduct(
            @PathVariable Long id,
            @PathVariable Integer quantity) {

        return productService.reserveProduct(id, quantity)
                .map(product ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Product quantity reserved successfully",
                                        product
                                )
                        )
                );
    }

    @PatchMapping("/{id}/release/{quantity}")
    public Mono<ResponseEntity<ApiResponse<ProductResponse>>> releaseProduct(
            @PathVariable Long id,
            @PathVariable Integer quantity) {

        return productService.releaseProduct(id, quantity)
                .map(product ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Product quantity released successfully",
                                        product
                                )
                        )
                );
    }
}
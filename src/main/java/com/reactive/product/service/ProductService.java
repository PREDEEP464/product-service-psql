package com.reactive.product.service;

import com.reactive.product.model.entity.request.ProductRequest;
import com.reactive.product.model.entity.response.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<ProductResponse> createProduct(ProductRequest request);

    Flux<ProductResponse> getAllProducts();

    Mono<ProductResponse> getProductById(Long id);

    Mono<ProductResponse> updateProduct(Long id, ProductRequest request);

    Mono<Void> deleteProduct(Long id);

    Mono<ProductResponse> activateProduct(Long id);

    Mono<ProductResponse> deactivateProduct(Long id);

    Mono<ProductResponse> reserveProduct(Long id, Integer quantity);

    Mono<ProductResponse> releaseProduct(Long id, Integer quantity);
}
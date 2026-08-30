package com.reactive.product.service;

import com.reactive.product.model.entity.request.ProductRequest;
import com.reactive.product.model.entity.request.StockUpdateRequest;
import com.reactive.product.model.entity.response.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<ProductResponse> createProduct(ProductRequest request);

    Flux<ProductResponse> getAllProducts();

    Mono<ProductResponse> getProductById(Long id);

    Mono<ProductResponse> updateProduct(Long id, ProductRequest request);

    Mono<ProductResponse> activateProduct(Long id);

    Mono<ProductResponse> deactivateProduct(Long id);

    Mono<ProductResponse> addStock(Long id, StockUpdateRequest request);

    Mono<ProductResponse> reserveProduct(Long id, Integer quantity);

    Mono<ProductResponse> releaseProduct(Long id, Integer quantity);

    Mono<Void> deleteProduct(Long id);
}
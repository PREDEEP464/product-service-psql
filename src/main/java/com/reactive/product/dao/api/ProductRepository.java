package com.reactive.product.dao.api;

import com.reactive.product.model.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Mono<Product> findByName(String name);

    Flux<Product> findByNameContainingIgnoreCase(String name);

    Flux<Product> findByIsActiveTrue();

    Mono<Product> findByIdAndIsActiveTrue(Long id);
}

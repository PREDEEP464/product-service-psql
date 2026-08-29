package com.reactive.product.dao.api;

import com.reactive.product.model.entity.Product;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Mono<Product> findByName(String name);

    Flux<Product> findByNameContainingIgnoreCase(String name);

    Mono<Boolean> existsByNameAndIsActiveTrue(String name);

    Flux<Product> findByIsActiveTrue();

    Mono<Product> findByIdAndIsActiveTrue(Long id);

    @Modifying
    @Query("""
            UPDATE products
            SET available_quantity = available_quantity - :quantity
            WHERE id = :id
              AND is_active = TRUE
              AND available_quantity >= :quantity
            """)
    Mono<Integer> reserveQuantity(Long id, Integer quantity);

    @Modifying
    @Query("""
            UPDATE products
            SET available_quantity = available_quantity + :quantity
            WHERE id = :id
              AND is_active = TRUE
              AND available_quantity + :quantity <= total_quantity
            """)
    Mono<Integer> releaseQuantity(Long id, Integer quantity);
}
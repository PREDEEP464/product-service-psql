package com.reactive.product.serviceImpl;

import com.reactive.product.dao.api.ProductRepository;
import com.reactive.product.exception.ProductNotFoundException;
import com.reactive.product.exception.InsufficientProductQuantityException;
import com.reactive.product.exception.ProductOperationException;
import com.reactive.product.model.entity.Product;
import com.reactive.product.model.entity.request.ProductRequest;
import com.reactive.product.model.entity.response.ProductResponse;
import com.reactive.product.service.ProductService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {

        return Mono.defer(() -> {

                    validateQuantity(request);

                    Product product = new Product();
                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPrice(request.getPrice());
                    product.setTotalQuantity(request.getTotalQuantity());
                    product.setAvailableQuantity(request.getAvailableQuantity());
                    product.setIsActive(request.getIsActive());

                    return productRepository.save(product);

                })
                .map(this::convertToResponse)
                .doOnNext(response ->
                        System.out.println("Product created successfully: " + response.getId()))
                .doOnError(error ->
                        System.err.println("Error while creating product: " + error.getMessage()));
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findByIsActiveTrue()
                .map(this::convertToResponse)
                .doOnNext(product ->
                        System.out.println("Product fetched: " + product.getId()))
                .doOnError(error ->
                        System.err.println("Error while fetching products: " + error.getMessage()));
    }

    @Override
    public Mono<ProductResponse> getProductById(Long id) {

        return productRepository.findByIdAndIsActiveTrue(id)
                .switchIfEmpty(
                        Mono.error(
                                new ProductNotFoundException(
                                        "Product not found with id: " + id
                                )
                        )
                )
                .map(this::convertToResponse)
                .doOnNext(response ->
                        System.out.println("Product found: " + response.getId()))
                .doOnError(error ->
                        System.err.println("Error while fetching product: " + error.getMessage()));
    }

    @Override
    public Mono<ProductResponse> updateProduct(Long id, ProductRequest request) {

        return productRepository.findByIdAndIsActiveTrue(id)
                .switchIfEmpty(
                        Mono.error(
                                new ProductNotFoundException(
                                        "Active product not found with id: " + id
                                )
                        )
                )
                .flatMap(product -> {

                    validateQuantity(request);

                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPrice(request.getPrice());
                    product.setTotalQuantity(request.getTotalQuantity());
                    product.setAvailableQuantity(request.getAvailableQuantity());
                    product.setIsActive(request.getIsActive());

                    return productRepository.save(product);
                })
                .map(this::convertToResponse)
                .doOnNext(response ->
                        System.out.println("Product updated successfully: " + response.getId()))
                .doOnError(error ->
                        System.err.println("Error while updating product: " + error.getMessage()));
    }

    @Override
    public Mono<Void> deleteProduct(Long id) {

        return productRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new ProductNotFoundException(
                                        "Product not found with id: " + id
                                )
                        )
                )
                .flatMap(product -> {
                    product.setIsActive(false);
                    return productRepository.save(product);
                })
                .then()
                .doOnError(error ->
                        System.err.println("Error while deactivating product: " + error.getMessage()));
    }

    private void validateQuantity(ProductRequest request) {

        if (request.getAvailableQuantity() > request.getTotalQuantity()) {
            throw new IllegalArgumentException(
                    "Available quantity cannot be greater than total quantity"
            );
        }
    }

    @Override
    public Mono<ProductResponse> activateProduct(Long id) {

        return productRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new ProductNotFoundException(
                                        "Product not found with id: " + id
                                )
                        )
                )
                .flatMap(product -> {

                    product.setIsActive(true);

                    return productRepository.save(product);
                })
                .map(this::convertToResponse)
                .doOnNext(response ->
                        System.out.println("Product activated successfully: " + response.getId()))
                .doOnError(error ->
                        System.err.println("Error while activating product: " + error.getMessage()));
    }

    @Override
    public Mono<ProductResponse> deactivateProduct(Long id) {

        return productRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new ProductNotFoundException(
                                        "Product not found with id: " + id
                                )
                        )
                )
                .flatMap(product -> {

                    product.setIsActive(false);

                    return productRepository.save(product);
                })
                .map(this::convertToResponse)
                .doOnNext(response ->
                        System.out.println("Product deactivated successfully: " + response.getId()))
                .doOnError(error ->
                        System.err.println("Error while deactivating product: " + error.getMessage()));
    }

    @Override
    public Mono<ProductResponse> reserveProduct(
            Long id,
            Integer quantity) {

        return productRepository.reserveQuantity(id, quantity)
                .flatMap(updatedRows -> {

                    if (updatedRows == 0) {
                        return Mono.error(
                                new InsufficientProductQuantityException(
                                        "Product not found, inactive, or insufficient quantity for product id: "
                                                + id
                                )
                        );
                    }

                    return productRepository.findById(id);
                })
                .switchIfEmpty(
                        Mono.error(
                                new ProductNotFoundException(
                                        "Product not found with id: " + id
                                )
                        )
                )
                .map(this::convertToResponse)
                .doOnNext(product ->
                        System.out.println(
                                "Product quantity reserved: "
                                        + product.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while reserving product quantity: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public Mono<ProductResponse> releaseProduct(
            Long id,
            Integer quantity) {

        return productRepository.releaseQuantity(id, quantity)
                .flatMap(updatedRows -> {

                    if (updatedRows == 0) {
                        return Mono.error(
                                new ProductOperationException(
                                        "Product release failed for product id: "
                                                + id
                                )
                        );
                    }

                    return productRepository.findById(id);
                })
                .switchIfEmpty(
                        Mono.error(
                                new ProductNotFoundException(
                                        "Product not found with id: " + id
                                )
                        )
                )
                .map(this::convertToResponse)
                .doOnNext(product ->
                        System.out.println(
                                "Product quantity released: "
                                        + product.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while releasing product quantity: "
                                        + error.getMessage()
                        )
                );
    }

    private ProductResponse convertToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getTotalQuantity(),
                product.getAvailableQuantity(),
                product.getIsActive()
        );
    }
}
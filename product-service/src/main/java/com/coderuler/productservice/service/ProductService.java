package com.coderuler.productservice.service;

import com.coderuler.productservice.model.Product;
import com.coderuler.productservice.respository.ProductRepository;
import com.coderuler.productservice.dto.ProductRequest;
import com.coderuler.productservice.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // ( for logging )
public class ProductService {
    // inject the ProductRepository into this ProductService class -> as we have to save the object in the database
    // (during the compile time, it automatically creates the required args constructor for
    // the ProductRepository and initializes the values)
    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
            .name(productRequest.getName())
            .description(productRequest.getDescription())
            .price(productRequest.getPrice())
            .build();

        // save the product into the database
        productRepository.save(product);
        log.info("Product {} is saved into the database.", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        // Need to map all the Product objects to ProductResponse objects
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}

package com.coderuler.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(value = "product")
public class Product {
    @Id // Id annotation from spring data -> tells that this is the unique identifier for this product
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}

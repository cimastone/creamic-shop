package com.ceramic.product.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 产品名称值对象
 */
@Getter
@EqualsAndHashCode
@ToString
public class ProductName {
    private final String value;
    
    public ProductName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Product name cannot exceed 100 characters");
        }
        this.value = value;
    }
} 
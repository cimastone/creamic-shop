package com.ceramic.product.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 产品类别值对象
 */
@Getter
@EqualsAndHashCode
@ToString
public class Category {
    private final String value;
    
    public Category(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("Category name cannot exceed 50 characters");
        }
        this.value = value;
    }
} 
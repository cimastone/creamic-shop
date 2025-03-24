package com.ceramic.product.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 产品ID值对象
 */
@Getter
@EqualsAndHashCode
@ToString
public class ProductId {
    private final Long value;
    
    public ProductId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        this.value = value;
    }
    
    public Long getValue() {
        return this.value;
    }
} 
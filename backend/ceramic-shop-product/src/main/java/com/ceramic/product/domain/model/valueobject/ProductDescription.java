package com.ceramic.product.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 产品描述值对象
 */
@Getter
@EqualsAndHashCode
@ToString
public class ProductDescription {
    private final String value;
    
    public ProductDescription(String value) {
        this.value = value;
    }
} 
package com.ceramic.product.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 图片URL值对象
 */
@Getter
@EqualsAndHashCode
@ToString
public class ImageUrl {
    private final String value;
    
    public ImageUrl(String value) {
        this.value = value;
    }
    
    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }
} 
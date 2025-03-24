package com.ceramic.product.domain.model.valueobject;

/**
 * 产品状态枚举
 */
public enum ProductStatus {
    ONLINE("上架"),
    OFFLINE("下架");
    
    private final String description;
    
    ProductStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 
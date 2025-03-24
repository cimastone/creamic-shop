package com.ceramic.product.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 库存值对象
 */
@Getter
@EqualsAndHashCode
@ToString
public class Inventory {
    private final int availableQuantity;
    
    public Inventory(int availableQuantity) {
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Inventory cannot be negative");
        }
        this.availableQuantity = availableQuantity;
    }
    
    public boolean isAvailable() {
        return availableQuantity > 0;
    }
    
    public boolean isAvailable(int requiredQuantity) {
        return availableQuantity >= requiredQuantity;
    }
    
    public Inventory decrease(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Decrease quantity cannot be negative");
        }
        if (quantity > availableQuantity) {
            throw new IllegalStateException("Not enough inventory");
        }
        return new Inventory(availableQuantity - quantity);
    }
    
    public Inventory increase(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Increase quantity cannot be negative");
        }
        return new Inventory(availableQuantity + quantity);
    }
} 
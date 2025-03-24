package com.ceramicshop.order.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项领域模型
 */
@Getter
public class OrderItem {
    
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private String productSpecs;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /**
     * 包级私有构造方法，用于反射和工厂方法
     */
    OrderItem() {
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
    }
    
    /**
     * 创建订单项
     */
    public static OrderItem create(
            Long productId, 
            String productName, 
            String productImage, 
            String productSpecs, 
            BigDecimal price, 
            Integer quantity) {
        
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("商品价格必须大于0");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        
        OrderItem orderItem = new OrderItem();
        orderItem.productId = productId;
        orderItem.productName = productName;
        orderItem.productImage = productImage;
        orderItem.productSpecs = productSpecs;
        orderItem.price = price;
        orderItem.quantity = quantity;
        orderItem.subtotal = price.multiply(new BigDecimal(quantity));
        
        return orderItem;
    }
    
    /**
     * 设置订单项ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 关联到订单
     */
    public void assignToOrder(Long orderId) {
        this.orderId = orderId;
    }
} 
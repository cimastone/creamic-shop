package com.ceramicshop.order.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    /**
     * 订单项ID
     */
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * 商品规格
     */
    private String productSpecs;
    
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 创建订单项
     */
    public static OrderItem createOrderItem(Long productId, String productName, String productImage, 
                                           String productSpecs, BigDecimal price, Integer quantity) {
        OrderItem orderItem = OrderItem.builder()
                .productId(productId)
                .productName(productName)
                .productImage(productImage)
                .productSpecs(productSpecs)
                .price(price)
                .quantity(quantity)
                .subtotal(price.multiply(new BigDecimal(quantity)))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        return orderItem;
    }
} 
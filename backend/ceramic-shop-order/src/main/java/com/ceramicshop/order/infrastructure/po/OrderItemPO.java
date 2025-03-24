package com.ceramicshop.order.infrastructure.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项持久化对象
 */
@Data
public class OrderItemPO {
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
} 
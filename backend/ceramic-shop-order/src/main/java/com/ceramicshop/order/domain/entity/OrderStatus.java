package com.ceramicshop.order.domain.entity;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    /**
     * 待支付
     */
    PENDING_PAYMENT,
    
    /**
     * 已支付
     */
    PAID,
    
    /**
     * 已发货
     */
    SHIPPED,
    
    /**
     * 已完成
     */
    COMPLETED,
    
    /**
     * 已取消
     */
    CANCELLED
} 
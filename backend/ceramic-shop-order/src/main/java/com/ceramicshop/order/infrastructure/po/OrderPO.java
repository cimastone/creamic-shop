package com.ceramicshop.order.infrastructure.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单持久化对象
 */
@Data
public class OrderPO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal paymentAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private Long addressId;
    private Long logisticsId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String shippingMethod;
    private String paymentMethod;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime completeTime;
    private LocalDateTime closeTime;
} 
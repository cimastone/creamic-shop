package com.ceramicshop.order.infrastructure.persistence.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单持久化对象
 */
@Data
public class OrderPO {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单编号
     */
    private String orderNumber;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;
    
    /**
     * 运费
     */
    private BigDecimal shippingFee;
    
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 发货时间
     */
    private LocalDateTime shipTime;
    
    /**
     * 送达时间
     */
    private LocalDateTime deliveryTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 关闭时间
     */
    private LocalDateTime closeTime;
    
    /**
     * 收货地址ID
     */
    private Long addressId;
    
    /**
     * 物流ID
     */
    private Long logisticsId;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人电话
     */
    private String receiverPhone;
    
    /**
     * 收货人地址
     */
    private String receiverAddress;
    
    /**
     * 配送方式
     */
    private String shippingMethod;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 
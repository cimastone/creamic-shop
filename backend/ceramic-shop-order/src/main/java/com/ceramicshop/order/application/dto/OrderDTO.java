package com.ceramicshop.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
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
     * 取消时间
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
    private String recipientName;
    
    /**
     * 收货人电话
     */
    private String recipientPhone;
    
    /**
     * 收货地址
     */
    private String recipientAddress;
    
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
     * 订单项列表
     */
    private List<OrderItemDTO> orderItems;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 
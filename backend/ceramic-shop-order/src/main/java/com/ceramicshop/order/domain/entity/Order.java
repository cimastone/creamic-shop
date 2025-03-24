package com.ceramicshop.order.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
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
    private OrderStatus status;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 发货时间
     */
    private LocalDateTime shipTime;
    
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
     * 备注
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
    
    /**
     * 订单项列表
     */
    private List<OrderItem> orderItems;
    
    /**
     * 创建订单
     */
    public static Order createOrder(Long userId, Long addressId, String remark, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(userId)
                .status(OrderStatus.PENDING_PAYMENT)
                .totalAmount(calculateTotalAmount(orderItems))
                .addressId(addressId)
                .remark(remark)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .orderItems(orderItems)
                .build();
        
        return order;
    }
    
    /**
     * 支付订单
     */
    public void pay() {
        if (this.status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("订单状态不正确");
        }
        
        this.status = OrderStatus.PAID;
        this.payTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 发货
     */
    public void ship(Long logisticsId) {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("订单状态不正确");
        }
        
        this.status = OrderStatus.SHIPPED;
        this.logisticsId = logisticsId;
        this.shipTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 确认收货
     */
    public void complete() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("订单状态不正确");
        }
        
        this.status = OrderStatus.COMPLETED;
        this.completeTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 取消订单
     */
    public void cancel() {
        if (this.status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("只有待支付订单可以取消");
        }
        
        this.status = OrderStatus.CANCELLED;
        this.closeTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 生成订单号
     */
    private static String generateOrderNumber() {
        return "ORDER" + System.currentTimeMillis();
    }
    
    /**
     * 计算订单总金额
     */
    private static BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 
package com.ceramicshop.order.domain.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单领域模型
 */
@Getter
public class Order {
    
    private Long id;
    private String orderNumber;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal paymentAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime completeTime;
    private LocalDateTime closeTime;
    private String shippingMethod;
    private String paymentMethod;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 移除简单的收货人信息，改用值对象
    // private Long addressId;
    // private String recipientName;
    // private String recipientPhone;
    // private String recipientAddress;
    
    // 添加收货地址值对象
    private ShippingAddress shippingAddress;
    
    private List<OrderItem> orderItems;
    
    /**
     * 包级私有构造方法，用于反射和工厂方法
     */
    Order() {
        this.orderItems = new ArrayList<>();
        this.status = OrderStatus.PENDING_PAYMENT;
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
    }
    
    /**
     * 创建新订单
     */
    public static Order create(Long userId, ShippingAddress shippingAddress, String remark, List<OrderItem> orderItems) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }
        
        if (shippingAddress == null) {
            throw new IllegalArgumentException("收货地址不能为空");
        }
        
        Order order = new Order();
        order.userId = userId;
        order.shippingAddress = shippingAddress;
        order.remark = remark;
        order.orderNumber = generateOrderNumber();
        order.totalAmount = calculateTotalAmount(orderItems);
        order.paymentAmount = order.totalAmount;
        order.shippingFee = BigDecimal.ZERO;
        order.discountAmount = BigDecimal.ZERO;
        
        // 设置默认值
        order.shippingMethod = "STANDARD";   // 默认配送方式
        order.paymentMethod = "ONLINE";      // 默认支付方式
        
        // 添加订单项
        orderItems.forEach(item -> {
            item.assignToOrder(order.id);
            order.orderItems.add(item);
        });
        
        return order;
    }
    
    /**
     * 支付订单
     */
    public void pay() {
        if (this.status != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("订单状态错误，当前状态：" + this.status);
        }
        
        this.status = OrderStatus.PAID;
        this.payTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 发货
     */
    public void ship() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("订单状态错误，当前状态：" + this.status);
        }
        
        this.status = OrderStatus.SHIPPED;
        this.shipTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 确认收货
     */
    public void complete() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("订单状态错误，当前状态：" + this.status);
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
            throw new IllegalStateException("订单状态错误，当前状态：" + this.status);
        }
        
        this.status = OrderStatus.CANCELLED;
        this.closeTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 设置订单ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 更新收货地址信息
     */
    public void updateShippingAddress(ShippingAddress shippingAddress) {
        if (shippingAddress == null) {
            throw new IllegalArgumentException("收货地址不能为空");
        }
        this.shippingAddress = shippingAddress;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 设置支付方式
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    /**
     * 生成订单编号
     */
    private static String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + 
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    /**
     * 计算订单总金额
     */
    private static BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 
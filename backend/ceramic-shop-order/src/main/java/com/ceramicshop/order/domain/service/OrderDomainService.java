package com.ceramicshop.order.domain.service;

import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderItem;

import java.util.List;

/**
 * 订单领域服务接口
 */
public interface OrderDomainService {
    
    /**
     * 创建订单
     */
    Order createOrder(Long userId, Long addressId, String remark, List<OrderItem> orderItems);
    
    /**
     * 支付订单
     */
    void payOrder(Order order);
    
    /**
     * 取消订单
     */
    void cancelOrder(Order order);
    
    /**
     * 发货
     */
    void shipOrder(Order order);
    
    /**
     * 确认收货
     */
    void completeOrder(Order order);
} 
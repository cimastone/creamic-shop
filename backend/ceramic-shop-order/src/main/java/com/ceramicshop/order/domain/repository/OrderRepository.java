package com.ceramicshop.order.domain.repository;

import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderStatus;

import java.util.List;

/**
 * 订单仓储接口
 */
public interface OrderRepository {
    
    /**
     * 保存订单
     */
    void save(Order order);
    
    /**
     * 根据ID查询订单
     */
    Order findById(Long id);
    
    /**
     * 根据订单号查询订单
     */
    Order findByOrderNumber(String orderNumber);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据用户ID和订单状态查询订单列表
     */
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    /**
     * 更新订单
     */
    void update(Order order);
    
    /**
     * 删除订单
     */
    void delete(Long id);
} 
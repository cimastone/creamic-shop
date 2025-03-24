package com.ceramicshop.order.infrastructure.mapper;

import com.ceramicshop.order.infrastructure.po.OrderItemPO;

import java.util.List;

/**
 * 订单项Mapper接口
 */
public interface OrderItemMapper {
    
    /**
     * 插入订单项
     */
    int insert(OrderItemPO orderItem);
    
    /**
     * 根据ID查询订单项
     */
    OrderItemPO selectById(Long id);
    
    /**
     * 根据订单ID查询订单项列表
     */
    List<OrderItemPO> selectByOrderId(Long orderId);
    
    /**
     * 更新订单项
     */
    int update(OrderItemPO orderItem);
    
    /**
     * 根据ID删除订单项
     */
    int deleteById(Long id);
    
    /**
     * 根据订单ID删除订单项
     */
    int deleteByOrderId(Long orderId);
} 
package com.ceramicshop.order.infrastructure.persistence.mapper;

import com.ceramicshop.order.infrastructure.persistence.entity.OrderItemPO;

import java.util.List;

/**
 * 订单项Mapper接口
 */
public interface OrderItemMapper {

    /**
     * 插入订单项
     *
     * @param orderItem 订单项对象
     * @return 影响行数
     */
    int insert(OrderItemPO orderItem);

    /**
     * 根据ID查询订单项
     *
     * @param id 订单项ID
     * @return 订单项对象
     */
    OrderItemPO selectById(Long id);

    /**
     * 根据订单ID查询订单项列表
     *
     * @param orderId 订单ID
     * @return 订单项列表
     */
    List<OrderItemPO> selectByOrderId(Long orderId);

    /**
     * 更新订单项
     *
     * @param orderItem 订单项对象
     * @return 影响行数
     */
    int update(OrderItemPO orderItem);

    /**
     * 根据ID删除订单项
     *
     * @param id 订单项ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据订单ID删除订单项
     *
     * @param orderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(Long orderId);
} 
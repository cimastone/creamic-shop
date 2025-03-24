package com.ceramicshop.order.infrastructure.mapper;

import com.ceramicshop.order.infrastructure.po.OrderPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
public interface OrderMapper {
    
    /**
     * 新增订单
     * 
     * @param order 订单PO
     * @return 影响行数
     */
    int insert(OrderPO order);
    
    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID
     * @return 订单PO
     */
    OrderPO selectById(Long id);
    
    /**
     * 根据订单号查询订单
     * 
     * @param orderNumber 订单号
     * @return 订单PO
     */
    OrderPO selectByOrderNumber(String orderNumber);
    
    /**
     * 根据用户ID查询订单列表
     * 
     * @param userId 用户ID
     * @return 订单PO列表
     */
    List<OrderPO> selectByUserId(Long userId);
    
    /**
     * 根据用户ID和订单状态查询订单列表
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单PO列表
     */
    List<OrderPO> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 更新订单
     * 
     * @param order 订单PO
     * @return 影响行数
     */
    int update(OrderPO order);
    
    /**
     * 根据ID删除订单
     * 
     * @param id 订单ID
     * @return 影响行数
     */
    int deleteById(Long id);
} 
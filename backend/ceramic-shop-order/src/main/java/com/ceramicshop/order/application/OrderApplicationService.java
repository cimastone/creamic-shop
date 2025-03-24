package com.ceramicshop.order.application;

import com.ceramicshop.order.application.dto.AddressDTO;
import com.ceramicshop.order.application.dto.OrderDTO;
import com.ceramicshop.order.application.dto.OrderItemDTO;
import com.ceramicshop.order.domain.model.OrderStatus;

import java.util.List;

/**
 * 订单应用服务接口
 */
public interface OrderApplicationService {
    
    /**
     * 创建订单
     * 
     * @param userId 用户ID
     * @param addressDTO 地址信息
     * @param orderItems 订单项列表
     * @return 订单DTO
     */
    OrderDTO createOrder(Long userId, AddressDTO addressDTO, List<OrderItemDTO> orderItems);
    
    /**
     * 根据ID获取订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID (用于校验权限)
     * @return 订单DTO
     */
    OrderDTO getOrderById(Long orderId, Long userId);
    
    /**
     * 获取用户所有订单
     * 
     * @param userId 用户ID
     * @return 订单DTO列表
     */
    List<OrderDTO> getUserOrders(Long userId);
    
    /**
     * 获取用户特定状态的订单
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单DTO列表
     */
    List<OrderDTO> getUserOrdersByStatus(Long userId, OrderStatus status);
    
    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID (用于校验权限)
     * @return 订单DTO
     */
    OrderDTO payOrder(Long orderId, Long userId);
    
    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID (用于校验权限)
     * @return 订单DTO
     */
    OrderDTO cancelOrder(Long orderId, Long userId);
    
    /**
     * 发货订单（管理员接口）
     * 
     * @param orderId 订单ID
     * @return 订单DTO
     */
    OrderDTO shipOrder(Long orderId);
    
    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID (用于校验权限)
     * @return 订单DTO
     */
    OrderDTO completeOrder(Long orderId, Long userId);
} 
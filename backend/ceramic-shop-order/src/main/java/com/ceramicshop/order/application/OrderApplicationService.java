package com.ceramicshop.order.application;

import com.ceramicshop.order.application.dto.AddressDTO;
import com.ceramicshop.order.application.dto.OrderDTO;
import com.ceramicshop.order.application.dto.OrderItemDTO;
import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderItem;
import com.ceramicshop.order.domain.model.OrderStatus;
import com.ceramicshop.order.domain.model.ShippingAddress;
import com.ceramicshop.order.domain.repository.OrderRepository;
import com.ceramicshop.order.domain.service.OrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单应用服务
 */
@Service
public class OrderApplicationService {
    
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final ShippingAddressService shippingAddressService;
    
    @Autowired
    public OrderApplicationService(OrderRepository orderRepository, 
                                  OrderDomainService orderDomainService,
                                  ShippingAddressService shippingAddressService) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.shippingAddressService = shippingAddressService;
    }
    
    /**
     * 创建订单
     * 
     * @param userId 用户ID
     * @param addressId 地址ID
     * @param orderItems 订单项列表
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO createOrder(Long userId, Long addressId, List<OrderItemDTO> orderItems) {
        // 验证参数
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (addressId == null) {
            throw new IllegalArgumentException("收货地址ID不能为空");
        }
        
        // 检查订单项
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }
        
        // 转换订单项
        List<OrderItem> domainOrderItems = orderItems.stream()
                .map(dto -> OrderItem.create(
                        dto.getProductId(),
                        dto.getProductName(),
                        dto.getProductImage(),
                        dto.getProductSpecs(),
                        dto.getPrice() != null ? dto.getPrice() : dto.getUnitPrice(),
                        dto.getQuantity()
                ))
                .collect(Collectors.toList());
        
        // 获取用户地址并转换为ShippingAddress值对象
        // 在实际项目中，这里应该调用用户服务获取地址信息
        ShippingAddress shippingAddress = fetchShippingAddressFromAddressId(userId, addressId);
        
        // 创建订单
        Order order = orderDomainService.createOrder(userId, shippingAddress, null, domainOrderItems);
        
        // 处理新订单业务逻辑
        orderDomainService.processNewOrder(order);
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        
        // 转换为DTO返回
        return convertToDTO(savedOrder);
    }
    
    /**
     * 从用户服务获取收货地址信息
     * 实际项目中，这里应该调用用户服务的API获取真实的地址信息
     */
    private ShippingAddress fetchShippingAddressFromAddressId(Long userId, Long addressId) {
        // 在实际项目中，这里应该调用用户服务获取地址信息
        // 这里模拟调用用户服务获取地址信息的过程
        
        try {
            // 模拟API调用延迟
            Thread.sleep(50);
            
            // 模拟从用户服务获取的地址信息
            return ShippingAddress.builder()
                    .receiverName("测试用户")
                    .receiverPhone("13800138000")
                    .province("广东省")
                    .city("深圳市")
                    .district("南山区")
                    .detailAddress("科技园路1号")
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取地址信息失败", e);
        }
    }
    
    /**
     * 根据ID获取订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID (用于校验权限)
     * @return 订单DTO
     */
    public OrderDTO getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权访问该订单");
        }
        
        return convertToDTO(order);
    }
    
    /**
     * 获取用户所有订单
     * 
     * @param userId 用户ID
     * @return 订单DTO列表
     */
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户特定状态的订单
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单DTO列表
     */
    public List<OrderDTO> getUserOrdersByStatus(Long userId, OrderStatus status) {
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 支付订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID (用于校验权限)
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO payOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该订单");
        }
        
        order.pay();
        Order updatedOrder = orderRepository.update(order);
        return convertToDTO(updatedOrder);
    }
    
    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID (用于校验权限)
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该订单");
        }
        
        order.cancel();
        Order updatedOrder = orderRepository.update(order);
        return convertToDTO(updatedOrder);
    }
    
    /**
     * 发货订单（管理员接口）
     * 
     * @param orderId 订单ID
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.ship();
        Order updatedOrder = orderRepository.update(order);
        return convertToDTO(updatedOrder);
    }
    
    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID (用于校验权限)
     * @return 订单DTO
     */
    @Transactional
    public OrderDTO completeOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该订单");
        }
        
        order.complete();
        Order updatedOrder = orderRepository.update(order);
        return convertToDTO(updatedOrder);
    }
    
    /**
     * 将订单领域模型转换为DTO
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .productSpecs(item.getProductSpecs())
                        .unitPrice(item.getPrice())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        
        // 准备地址信息
        Long addressId = null;
        String recipientName = null;
        String recipientPhone = null;
        String recipientAddress = null;
        
        // 从ShippingAddress获取地址信息
        if (order.getShippingAddress() != null) {
            ShippingAddress address = order.getShippingAddress();
            addressId = address.getId();
            recipientName = address.getReceiverName();
            recipientPhone = address.getReceiverPhone();
            recipientAddress = address.getFullAddress();
        }
        
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .paymentAmount(order.getPaymentAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .addressId(addressId)
                .recipientName(recipientName)
                .recipientPhone(recipientPhone)
                .recipientAddress(recipientAddress)
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .payTime(order.getPayTime())
                .shipTime(order.getShipTime())
                .deliveryTime(order.getDeliveryTime())
                .completeTime(order.getCompleteTime())
                .closeTime(order.getCloseTime())
                .orderItems(orderItemDTOs)
                .build();
    }
} 
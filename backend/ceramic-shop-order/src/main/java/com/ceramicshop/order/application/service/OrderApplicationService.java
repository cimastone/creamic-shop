package com.ceramicshop.order.application.service;

import com.ceramicshop.order.application.dto.OrderDTO;
import com.ceramicshop.order.application.dto.OrderItemDTO;
import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderItem;
import com.ceramicshop.order.domain.repository.OrderRepository;
import com.ceramicshop.order.domain.service.OrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    
    @Autowired
    public OrderApplicationService(OrderRepository orderRepository, OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
    }
    
    /**
     * 创建订单
     */
    @Transactional
    public OrderDTO createOrder(Long userId, Long addressId, String remark, List<OrderItemDTO> orderItemDTOs) {
        if (orderItemDTOs == null || orderItemDTOs.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }
        
        // 转换订单项
        List<OrderItem> orderItems = orderItemDTOs.stream()
                .map(dto -> OrderItem.create(
                        dto.getProductId(),
                        dto.getProductName(),
                        dto.getProductImage(),
                        dto.getProductSpecs(),
                        dto.getUnitPrice(),
                        dto.getQuantity()
                ))
                .collect(Collectors.toList());
        
        // 创建订单
        Order order = orderDomainService.createOrder(userId, addressId, remark, orderItems);
        
        // 保存订单
        orderRepository.save(order);
        
        // 转换为DTO返回
        return convertToDTO(order);
    }
    
    /**
     * 获取订单详情
     */
    public OrderDTO getOrderDetail(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        return convertToDTO(orderOpt.get());
    }
    
    /**
     * 获取用户订单列表
     */
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据状态获取用户订单列表
     */
    public List<OrderDTO> getUserOrdersByStatus(Long userId, String status) {
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status);
        
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 支付订单
     */
    @Transactional
    public OrderDTO payOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        Order order = orderOpt.get();
        orderDomainService.payOrder(order);
        orderRepository.save(order);
        
        return convertToDTO(order);
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        Order order = orderOpt.get();
        orderDomainService.cancelOrder(order);
        orderRepository.save(order);
        
        return convertToDTO(order);
    }
    
    /**
     * 发货
     */
    @Transactional
    public OrderDTO shipOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        Order order = orderOpt.get();
        orderDomainService.shipOrder(order);
        orderRepository.save(order);
        
        return convertToDTO(order);
    }
    
    /**
     * 确认收货
     */
    @Transactional
    public OrderDTO completeOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        Order order = orderOpt.get();
        orderDomainService.completeOrder(order);
        orderRepository.save(order);
        
        return convertToDTO(order);
    }
    
    /**
     * 将领域模型转换为DTO
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
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .paymentAmount(order.getPaymentAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .payTime(order.getPayTime())
                .shipTime(order.getShipTime())
                .deliveryTime(order.getDeliveryTime())
                .completeTime(order.getCompleteTime())
                .closeTime(order.getCloseTime())
                .addressId(order.getAddressId())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .recipientAddress(order.getRecipientAddress())
                .shippingMethod(order.getShippingMethod())
                .paymentMethod(order.getPaymentMethod())
                .remark(order.getRemark())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .orderItems(orderItemDTOs)
                .build();
    }
} 
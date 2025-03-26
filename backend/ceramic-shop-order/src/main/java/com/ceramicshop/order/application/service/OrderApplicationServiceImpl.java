package com.ceramicshop.order.application.service;

import com.ceramicshop.order.application.OrderApplicationService;
import com.ceramicshop.order.application.dto.AddressDTO;
import com.ceramicshop.order.application.dto.OrderDTO;
import com.ceramicshop.order.application.dto.OrderItemDTO;
import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderItem;
import com.ceramicshop.order.domain.model.OrderStatus;
import com.ceramicshop.order.domain.repository.OrderRepository;
import com.ceramicshop.order.domain.service.OrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单应用服务实现类
 */
@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {
    
    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    
    @Autowired
    public OrderApplicationServiceImpl(OrderRepository orderRepository, OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
    }
    
    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, Long addressId, List<OrderItemDTO> orderItemDTOs) {
        // 验证参数
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (addressId == null) {
            throw new IllegalArgumentException("收货地址ID不能为空");
        }
        
        // 检查订单项
        if (orderItemDTOs == null || orderItemDTOs.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }
        
        // 打印订单项进行调试
        System.out.println("通过地址ID创建订单 - 订单项数量: " + orderItemDTOs.size());
        orderItemDTOs.forEach(item -> {
            System.out.println("商品ID: " + item.getProductId());
            System.out.println("商品名称: " + item.getProductName());
            System.out.println("商品数量: " + item.getQuantity());
            System.out.println("商品单价: " + item.getUnitPrice());
        });
        
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
        
        // 创建订单 - 使用地址ID
        Order order = Order.create(userId, addressId, null, orderItems);
        
        // 处理新订单业务逻辑
        orderDomainService.processNewOrder(order);
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        
        // 转换为DTO返回
        return convertToDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO createOrder(Long userId, AddressDTO addressDTO, List<OrderItemDTO> orderItemDTOs) {
        // 验证参数
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (addressDTO == null) {
            throw new IllegalArgumentException("收货地址信息不能为空");
        }
        
        if (addressDTO.getReceiverName() == null || addressDTO.getReceiverName().isEmpty()) {
            throw new IllegalArgumentException("收货人姓名不能为空");
        }
        
        if (addressDTO.getReceiverPhone() == null || addressDTO.getReceiverPhone().isEmpty()) {
            throw new IllegalArgumentException("收货人电话不能为空");
        }
        
        if (addressDTO.getReceiverAddress() == null || addressDTO.getReceiverAddress().isEmpty()) {
            throw new IllegalArgumentException("收货地址不能为空");
        }
        
        // 检查订单项
        if (orderItemDTOs == null || orderItemDTOs.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }
        
        // 打印订单项进行调试
        System.out.println("直接使用收货人信息创建订单 - 订单项数量: " + orderItemDTOs.size());
        orderItemDTOs.forEach(item -> {
            System.out.println("商品ID: " + item.getProductId());
            System.out.println("商品名称: " + item.getProductName());
            System.out.println("商品数量: " + item.getQuantity());
            System.out.println("商品单价: " + item.getUnitPrice());
        });
        
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
        
        // 创建订单 - 使用null作为地址ID（因为我们直接提供了收货人信息）
        Order order = Order.create(userId, null, null, orderItems);
        
        // 设置收货人信息
        order.setRecipientInfo(
                addressDTO.getReceiverName(),
                addressDTO.getReceiverPhone(),
                addressDTO.getReceiverAddress()
        );
        
        // 处理新订单业务逻辑
        orderDomainService.processNewOrder(order);
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        
        // 转换为DTO返回
        return convertToDTO(savedOrder);
    }
    
    @Override
    public OrderDTO getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权访问该订单");
        }
        
        return convertToDTO(order);
    }
    
    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderDTO> getUserOrdersByStatus(Long userId, OrderStatus status) {
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public OrderDTO payOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该订单");
        }
        
        order.pay();
        Order savedOrder = orderRepository.save(order);
        
        return convertToDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该订单");
        }
        
        order.cancel();
        Order savedOrder = orderRepository.save(order);
        
        return convertToDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        order.ship();
        Order savedOrder = orderRepository.save(order);
        
        return convertToDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderDTO completeOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权操作该订单");
        }
        
        order.complete();
        Order savedOrder = orderRepository.save(order);
        
        return convertToDTO(savedOrder);
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
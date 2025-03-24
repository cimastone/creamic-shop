package com.ceramicshop.order.infrastructure.repository;

import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderItem;
import com.ceramicshop.order.domain.model.OrderStatus;
import com.ceramicshop.order.domain.repository.OrderRepository;
import com.ceramicshop.order.infrastructure.mapper.OrderItemMapper;
import com.ceramicshop.order.infrastructure.mapper.OrderMapper;
import com.ceramicshop.order.infrastructure.po.OrderItemPO;
import com.ceramicshop.order.infrastructure.po.OrderPO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单仓储实现类
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderRepositoryImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public void save(Order order) {
        // 转换订单为PO
        OrderPO orderPO = toPO(order);
        
        // 保存订单
        orderMapper.insert(orderPO);
        
        // 设置订单ID
        order.setId(orderPO.getId());
        
        // 保存订单项
        for (OrderItem item : order.getOrderItems()) {
            OrderItemPO itemPO = toPO(item);
            itemPO.setOrderId(orderPO.getId());
            orderItemMapper.insert(itemPO);
        }
    }

    @Override
    public Order findById(Long id) {
        // 查询订单
        OrderPO orderPO = orderMapper.selectById(id);
        if (orderPO == null) {
            return null;
        }
        
        // 查询订单项
        List<OrderItemPO> itemPOs = orderItemMapper.selectByOrderId(id);
        
        // 转换为领域模型
        return toDomain(orderPO, itemPOs);
    }

    @Override
    public Order findByOrderNumber(String orderNumber) {
        // 查询订单
        OrderPO orderPO = orderMapper.selectByOrderNumber(orderNumber);
        if (orderPO == null) {
            return null;
        }
        
        // 查询订单项
        List<OrderItemPO> itemPOs = orderItemMapper.selectByOrderId(orderPO.getId());
        
        // 转换为领域模型
        return toDomain(orderPO, itemPOs);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        // 查询用户的所有订单
        List<OrderPO> orderPOs = orderMapper.selectByUserId(userId);
        if (orderPOs == null || orderPOs.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 转换为领域模型
        List<Order> orders = new ArrayList<>(orderPOs.size());
        for (OrderPO orderPO : orderPOs) {
            List<OrderItemPO> itemPOs = orderItemMapper.selectByOrderId(orderPO.getId());
            orders.add(toDomain(orderPO, itemPOs));
        }
        
        return orders;
    }

    @Override
    public List<Order> findByUserIdAndStatus(Long userId, OrderStatus status) {
        // 查询用户的特定状态订单
        List<OrderPO> orderPOs = orderMapper.selectByUserIdAndStatus(userId, status.name());
        if (orderPOs == null || orderPOs.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 转换为领域模型
        List<Order> orders = new ArrayList<>(orderPOs.size());
        for (OrderPO orderPO : orderPOs) {
            List<OrderItemPO> itemPOs = orderItemMapper.selectByOrderId(orderPO.getId());
            orders.add(toDomain(orderPO, itemPOs));
        }
        
        return orders;
    }

    @Override
    @Transactional
    public void update(Order order) {
        // 转换为PO并更新
        OrderPO orderPO = toPO(order);
        orderMapper.update(orderPO);
        
        // 更新订单项（如有需要）
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 删除订单项
        orderItemMapper.deleteByOrderId(id);
        
        // 删除订单
        orderMapper.deleteById(id);
    }
    
    /**
     * 将订单PO转换为领域模型
     */
    private Order toDomain(OrderPO po, List<OrderItemPO> itemPOs) {
        try {
            // 使用反射获取包级私有构造方法
            Constructor<Order> constructor = Order.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Order order = constructor.newInstance();
            
            // 设置基本字段
            setFieldValue(order, "id", po.getId());
            setFieldValue(order, "orderNumber", po.getOrderNumber());
            setFieldValue(order, "userId", po.getUserId());
            setFieldValue(order, "status", OrderStatus.valueOf(po.getStatus()));
            setFieldValue(order, "totalAmount", po.getTotalAmount());
            setFieldValue(order, "paymentAmount", po.getTotalAmount()); // 简化处理
            setFieldValue(order, "recipientName", po.getReceiverName());
            setFieldValue(order, "recipientPhone", po.getReceiverPhone());
            setFieldValue(order, "recipientAddress", po.getReceiverAddress());
            setFieldValue(order, "createTime", po.getCreateTime());
            setFieldValue(order, "updateTime", po.getUpdateTime());
            setFieldValue(order, "payTime", po.getPayTime());
            setFieldValue(order, "shipTime", po.getShipTime());
            setFieldValue(order, "completeTime", po.getCompleteTime());
            setFieldValue(order, "closeTime", po.getCancelTime());
            
            // 设置订单项
            List<OrderItem> items = new ArrayList<>();
            if (itemPOs != null && !itemPOs.isEmpty()) {
                for (OrderItemPO itemPO : itemPOs) {
                    items.add(toDomain(itemPO));
                }
            }
            setFieldValue(order, "orderItems", items);
            
            return order;
        } catch (Exception e) {
            throw new RuntimeException("转换订单领域模型失败", e);
        }
    }
    
    /**
     * 将订单项PO转换为领域模型
     */
    private OrderItem toDomain(OrderItemPO po) {
        try {
            // 使用反射获取包级私有构造方法
            Constructor<OrderItem> constructor = OrderItem.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            OrderItem item = constructor.newInstance();
            
            // 设置基本字段
            setFieldValue(item, "id", po.getId());
            setFieldValue(item, "orderId", po.getOrderId());
            setFieldValue(item, "productId", po.getProductId());
            setFieldValue(item, "productName", po.getProductName());
            setFieldValue(item, "productImage", po.getProductImage());
            setFieldValue(item, "productSpecs", po.getProductSpecs());
            setFieldValue(item, "price", po.getPrice());
            setFieldValue(item, "quantity", po.getQuantity());
            setFieldValue(item, "subtotal", po.getSubtotal());
            setFieldValue(item, "createTime", po.getCreateTime());
            setFieldValue(item, "updateTime", po.getUpdateTime());
            
            return item;
        } catch (Exception e) {
            throw new RuntimeException("转换订单项领域模型失败", e);
        }
    }
    
    /**
     * 将订单领域模型转换为PO
     */
    private OrderPO toPO(Order order) {
        OrderPO po = new OrderPO();
        po.setId(order.getId());
        po.setOrderNumber(order.getOrderNumber());
        po.setUserId(order.getUserId());
        po.setStatus(order.getStatus().name());
        po.setTotalAmount(order.getTotalAmount());
        po.setReceiverName(order.getRecipientName());
        po.setReceiverPhone(order.getRecipientPhone());
        po.setReceiverAddress(order.getRecipientAddress());
        po.setCreateTime(order.getCreateTime());
        po.setUpdateTime(LocalDateTime.now());
        po.setPayTime(order.getPayTime());
        po.setShipTime(order.getShipTime());
        po.setCompleteTime(order.getCompleteTime());
        po.setCancelTime(order.getCloseTime());
        return po;
    }
    
    /**
     * 将订单项领域模型转换为PO
     */
    private OrderItemPO toPO(OrderItem item) {
        OrderItemPO po = new OrderItemPO();
        po.setId(item.getId());
        po.setOrderId(item.getOrderId());
        po.setProductId(item.getProductId());
        po.setProductName(item.getProductName());
        po.setProductImage(item.getProductImage());
        po.setProductSpecs(item.getProductSpecs());
        po.setPrice(item.getPrice());
        po.setQuantity(item.getQuantity());
        po.setSubtotal(item.getSubtotal());
        po.setCreateTime(item.getCreateTime());
        po.setUpdateTime(LocalDateTime.now());
        return po;
    }
    
    /**
     * 使用反射设置字段值
     */
    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("设置字段值失败: " + fieldName, e);
        }
    }
} 
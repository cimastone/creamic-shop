package com.ceramicshop.order.infrastructure.persistence.repository;

import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderItem;
import com.ceramicshop.order.domain.model.OrderStatus;
import com.ceramicshop.order.domain.model.ShippingAddress;
import com.ceramicshop.order.domain.repository.OrderRepository;
import com.ceramicshop.order.infrastructure.persistence.mapper.OrderItemMapper;
import com.ceramicshop.order.infrastructure.persistence.mapper.OrderMapper;
import com.ceramicshop.order.infrastructure.persistence.mapper.ShippingAddressMapper;
import com.ceramicshop.order.infrastructure.persistence.entity.OrderItemPO;
import com.ceramicshop.order.infrastructure.persistence.entity.OrderPO;
import com.ceramicshop.order.infrastructure.persistence.entity.ShippingAddressPO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 订单仓储实现类
 */
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShippingAddressMapper shippingAddressMapper;

    public OrderRepositoryImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, ShippingAddressMapper shippingAddressMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.shippingAddressMapper = shippingAddressMapper;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        // 转换订单为PO
        OrderPO orderPO = toPO(order);
        
        // 保存订单
        orderMapper.insert(orderPO);
        
        // 设置订单ID
        order.setId(orderPO.getId());
        
        // 保存物流地址（值对象）
        if (order.getShippingAddress() != null) {
            ShippingAddressPO addressPO = toShippingAddressPO(order.getShippingAddress());
            addressPO.setOrderId(order.getId());
            shippingAddressMapper.insert(addressPO);
        }
        
        // 保存订单项
        for (OrderItem item : order.getOrderItems()) {
            OrderItemPO itemPO = toPO(item);
            itemPO.setOrderId(orderPO.getId());
            orderItemMapper.insert(itemPO);
        }
        
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        // 查询订单
        OrderPO orderPO = orderMapper.selectById(id);
        if (orderPO == null) {
            return Optional.empty();
        }
        
        // 查询订单项
        List<OrderItemPO> itemPOs = orderItemMapper.selectByOrderId(id);
        
        // 查询物流地址
        ShippingAddressPO addressPO = shippingAddressMapper.selectByOrderId(id);
        
        // 转换为领域模型
        return Optional.of(toDomain(orderPO, itemPOs, addressPO));
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        // 查询订单
        OrderPO orderPO = orderMapper.selectByOrderNumber(orderNumber);
        if (orderPO == null) {
            return Optional.empty();
        }
        
        // 查询订单项
        List<OrderItemPO> itemPOs = orderItemMapper.selectByOrderId(orderPO.getId());
        
        // 查询物流地址
        ShippingAddressPO addressPO = shippingAddressMapper.selectByOrderId(orderPO.getId());
        
        // 转换为领域模型
        return Optional.of(toDomain(orderPO, itemPOs, addressPO));
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
            ShippingAddressPO addressPO = shippingAddressMapper.selectByOrderId(orderPO.getId());
            orders.add(toDomain(orderPO, itemPOs, addressPO));
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
            ShippingAddressPO addressPO = shippingAddressMapper.selectByOrderId(orderPO.getId());
            orders.add(toDomain(orderPO, itemPOs, addressPO));
        }
        
        return orders;
    }

    @Override
    @Transactional
    public Order update(Order order) {
        // 转换为PO并更新
        OrderPO orderPO = toPO(order);
        orderMapper.update(orderPO);
        
        // 更新物流地址
        if (order.getShippingAddress() != null) {
            ShippingAddressPO addressPO = toShippingAddressPO(order.getShippingAddress());
            addressPO.setOrderId(order.getId());
            
            // 检查地址是否存在
            ShippingAddressPO existingAddress = shippingAddressMapper.selectByOrderId(order.getId());
            if (existingAddress != null) {
                addressPO.setId(existingAddress.getId());
                shippingAddressMapper.update(addressPO);
            } else {
                shippingAddressMapper.insert(addressPO);
            }
        }
        
        // 更新订单项（如有需要）
        
        return order;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 删除订单项
        orderItemMapper.deleteByOrderId(id);
        
        // 删除物流地址
        shippingAddressMapper.deleteByOrderId(id);
        
        // 删除订单
        orderMapper.deleteById(id);
    }
    
    /**
     * 将订单PO转换为领域模型
     */
    private Order toDomain(OrderPO po, List<OrderItemPO> itemPOs, ShippingAddressPO addressPO) {
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
            setFieldValue(order, "paymentAmount", po.getPaymentAmount());
            setFieldValue(order, "shippingFee", po.getShippingFee());
            setFieldValue(order, "discountAmount", po.getDiscountAmount());
            setFieldValue(order, "shippingMethod", po.getShippingMethod());
            setFieldValue(order, "paymentMethod", po.getPaymentMethod());
            setFieldValue(order, "remark", po.getRemark());
            setFieldValue(order, "createTime", po.getCreateTime());
            setFieldValue(order, "updateTime", po.getUpdateTime());
            setFieldValue(order, "payTime", po.getPayTime());
            setFieldValue(order, "shipTime", po.getShipTime());
            setFieldValue(order, "deliveryTime", po.getDeliveryTime());
            setFieldValue(order, "completeTime", po.getCompleteTime());
            setFieldValue(order, "closeTime", po.getCloseTime());
            
            // 设置物流地址值对象
            if (addressPO != null) {
                ShippingAddress address = toShippingAddressDomain(addressPO);
                setFieldValue(order, "shippingAddress", address);
            }
            
            // 设置订单项
            List<OrderItem> items = new ArrayList<>();
            if (itemPOs != null && !itemPOs.isEmpty()) {
                for (OrderItemPO itemPO : itemPOs) {
                    OrderItem item = toDomainOrderItem(itemPO);
                    items.add(item);
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
    private OrderItem toDomainOrderItem(OrderItemPO po) {
        try {
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
     * 使用反射设置对象字段值
     */
    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("设置" + fieldName + "字段失败", e);
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
        po.setPaymentAmount(order.getPaymentAmount());
        po.setShippingFee(order.getShippingFee());
        po.setDiscountAmount(order.getDiscountAmount());
        po.setPayTime(order.getPayTime());
        po.setShipTime(order.getShipTime());
        po.setDeliveryTime(order.getDeliveryTime());
        po.setCompleteTime(order.getCompleteTime());
        po.setCloseTime(order.getCloseTime());
        po.setShippingMethod(order.getShippingMethod());
        po.setPaymentMethod(order.getPaymentMethod());
        po.setRemark(order.getRemark());
        po.setCreateTime(order.getCreateTime());
        po.setUpdateTime(order.getUpdateTime());
        return po;
    }
    
    /**
     * 将物流地址值对象转换为PO
     */
    private ShippingAddressPO toShippingAddressPO(ShippingAddress address) {
        ShippingAddressPO po = new ShippingAddressPO();
        po.setId(address.getId());
        po.setOrderId(address.getOrderId());
        po.setReceiverName(address.getReceiverName());
        po.setReceiverPhone(address.getReceiverPhone());
        po.setProvince(address.getProvince());
        po.setCity(address.getCity());
        po.setDistrict(address.getDistrict());
        po.setDetailAddress(address.getDetailAddress());
        po.setCreateTime(address.getCreateTime());
        po.setUpdateTime(address.getUpdateTime());
        return po;
    }
    
    /**
     * 将物流地址PO转换为值对象
     */
    private ShippingAddress toShippingAddressDomain(ShippingAddressPO po) {
        return ShippingAddress.builder()
                .id(po.getId())
                .orderId(po.getOrderId())
                .receiverName(po.getReceiverName())
                .receiverPhone(po.getReceiverPhone())
                .province(po.getProvince())
                .city(po.getCity())
                .district(po.getDistrict())
                .detailAddress(po.getDetailAddress())
                .createTime(po.getCreateTime())
                .updateTime(po.getUpdateTime())
                .build();
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
} 
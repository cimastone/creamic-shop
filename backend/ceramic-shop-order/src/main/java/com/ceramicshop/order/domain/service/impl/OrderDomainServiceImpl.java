package com.ceramicshop.order.domain.service.impl;

import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderItem;
import com.ceramicshop.order.domain.model.ShippingAddress;
import com.ceramicshop.order.domain.service.OrderDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单领域服务实现类
 */
@Service
public class OrderDomainServiceImpl implements OrderDomainService {
    
    @Override
    public Order createOrder(Long userId, ShippingAddress shippingAddress, String remark, List<OrderItem> orderItems) {
        return Order.create(userId, shippingAddress, remark, orderItems);
    }
    
    @Override
    public void payOrder(Order order) {
        order.pay();
    }
    
    @Override
    public void cancelOrder(Order order) {
        order.cancel();
    }
    
    @Override
    public void shipOrder(Order order) {
        order.ship();
    }
    
    @Override
    public void completeOrder(Order order) {
        order.complete();
    }

    @Override
    public void processNewOrder(Order order) {
        // 这里可以添加一些业务逻辑，比如:
        // 1. 检查库存
        // 2. 计算优惠
        // 3. 计算运费
        // 4. 其他业务规则
    }
} 
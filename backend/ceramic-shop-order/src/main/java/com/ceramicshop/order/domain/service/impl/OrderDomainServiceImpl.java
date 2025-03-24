package com.ceramicshop.order.domain.service.impl;

import com.ceramicshop.order.domain.model.Order;
import com.ceramicshop.order.domain.model.OrderItem;
import com.ceramicshop.order.domain.service.OrderDomainService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单领域服务实现类
 */
@Service
public class OrderDomainServiceImpl implements OrderDomainService {
    
    @Override
    public Order createOrder(Long userId, Long addressId, String remark, List<OrderItem> orderItems) {
        return Order.create(userId, addressId, remark, orderItems);
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
} 
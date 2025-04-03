package com.ceramicshop.order.domain.repository;

import com.ceramicshop.order.domain.model.ShippingAddress;

import java.util.Optional;

/**
 * 物流地址仓储接口
 */
public interface ShippingAddressRepository {
    
    /**
     * 保存物流地址
     */
    ShippingAddress save(ShippingAddress address);
    
    /**
     * 根据ID查询物流地址
     */
    Optional<ShippingAddress> findById(Long id);
    
    /**
     * 根据订单ID查询物流地址
     */
    Optional<ShippingAddress> findByOrderId(Long orderId);
    
    /**
     * 删除物流地址
     */
    void delete(Long id);
} 
package com.ceramicshop.user.domain.repository;

import com.ceramicshop.user.domain.model.ShippingAddress;

import java.util.List;
import java.util.Optional;

/**
 * 收货地址仓储接口
 */
public interface ShippingAddressRepository {
    
    /**
     * 保存收货地址
     */
    ShippingAddress save(ShippingAddress address);
    
    /**
     * 根据ID查询收货地址
     */
    Optional<ShippingAddress> findById(Long id);
    
    /**
     * 根据用户ID查询收货地址列表
     */
    List<ShippingAddress> findByUserId(Long userId);
    
    /**
     * 查询用户的默认收货地址
     */
    Optional<ShippingAddress> findDefaultByUserId(Long userId);
    
    /**
     * 删除收货地址
     */
    void delete(Long id);
    
    /**
     * 将指定地址设为默认，并取消其他默认地址
     */
    void setDefault(Long addressId, Long userId);
} 
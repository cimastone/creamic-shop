package com.ceramicshop.user.application;

import com.ceramicshop.user.application.dto.ShippingAddressDTO;

import java.util.List;

/**
 * 收货地址应用服务接口
 */
public interface ShippingAddressService {
    
    /**
     * 创建收货地址
     */
    ShippingAddressDTO createAddress(Long userId, ShippingAddressDTO addressDTO);
    
    /**
     * 更新收货地址
     */
    ShippingAddressDTO updateAddress(Long id, ShippingAddressDTO addressDTO);
    
    /**
     * 根据ID获取收货地址
     */
    ShippingAddressDTO getAddressById(Long id);
    
    /**
     * 获取用户收货地址列表
     */
    List<ShippingAddressDTO> getAddressesByUserId(Long userId);
    
    /**
     * 删除收货地址
     */
    void deleteAddress(Long id);
    
    /**
     * 设置默认收货地址
     */
    void setDefaultAddress(Long id, Long userId);
    
    /**
     * 获取用户默认收货地址
     */
    ShippingAddressDTO getDefaultAddress(Long userId);
} 
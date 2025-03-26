package com.ceramicshop.user.infrastructure.mapper;

import com.ceramicshop.user.infrastructure.po.ShippingAddressPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收货地址Mapper接口
 */
public interface ShippingAddressMapper {
    
    /**
     * 新增收货地址
     */
    int insert(ShippingAddressPO address);
    
    /**
     * 更新收货地址
     */
    int update(ShippingAddressPO address);
    
    /**
     * 根据ID查询收货地址
     */
    ShippingAddressPO selectById(Long id);
    
    /**
     * 根据用户ID查询收货地址列表
     */
    List<ShippingAddressPO> selectByUserId(Long userId);
    
    /**
     * 根据用户ID查询默认收货地址
     */
    ShippingAddressPO selectDefaultByUserId(Long userId);
    
    /**
     * 根据ID删除收货地址
     */
    int deleteById(Long id);
    
    /**
     * 取消指定用户的所有默认地址
     */
    int cancelDefaultByUserId(Long userId);
    
    /**
     * 设置指定地址为默认地址
     */
    int setDefault(@Param("id") Long id, @Param("userId") Long userId);
} 
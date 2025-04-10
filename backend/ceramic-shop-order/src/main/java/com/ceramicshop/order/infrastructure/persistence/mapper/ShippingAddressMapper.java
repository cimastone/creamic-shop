package com.ceramicshop.order.infrastructure.persistence.mapper;

import com.ceramicshop.order.infrastructure.persistence.entity.ShippingAddressPO;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 物流地址Mapper接口
 */
public interface ShippingAddressMapper {
    
    /**
     * 插入物流地址
     */
    int insert(ShippingAddressPO address);
    
    /**
     * 更新物流地址
     */
    int update(ShippingAddressPO address);
    
    /**
     * 根据ID查询物流地址
     */
    ShippingAddressPO selectById(Long id);
    
    /**
     * 根据订单ID查询物流地址
     */
    ShippingAddressPO selectByOrderId(Long orderId);
    
    /**
     * 根据ID删除物流地址
     */
    int deleteById(Long id);
    
    /**
     * 根据订单ID删除物流地址
     */
    int deleteByOrderId(Long orderId);
} 